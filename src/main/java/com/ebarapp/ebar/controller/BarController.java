package com.ebarapp.ebar.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.ebarapp.ebar.model.*;
import com.ebarapp.ebar.model.dtos.BarCapacity;
import com.ebarapp.ebar.model.dtos.BarCreateDTO;
import com.ebarapp.ebar.model.dtos.BarDTO;
import com.ebarapp.ebar.service.ClientService;
import com.ebarapp.ebar.service.DBImageService;
import com.ebarapp.ebar.service.UserService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.ebarapp.ebar.service.BarService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600, exposedHeaders = "location")
@RestController
@RequestMapping("/api/bar")
public class BarController {

    @Autowired
    private BarService barService;

    @Autowired
    private DBImageService dbImageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    private static final String ROLE_OWNER = "ROLE_OWNER";

    private static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

    private static final String ROLE_CLIENT = "ROLE_CLIENT";

    @PostMapping("")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Bar> createBar(@Valid @RequestBody BarCreateDTO newBarCreateDTO) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        Optional<User> optionalUser = this.userService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Set<Bar> bars = new HashSet<>();
        var user = optionalUser.get();
        var owner = new Owner(user.getUsername(), user.getFirstName(), user.getLastName(), user.getDni(), user.getEmail(), user.getPhoneNumber(), user.getPassword(), user.getRoles(), bars);
        var newBar = new Bar(newBarCreateDTO);
        newBar.setOwner(owner);
        bars.add(newBar);
        owner.setBar(bars);

        this.barService.createBar(newBar);
        return ResponseEntity.created(URI.create("/bares/" + newBar.getId())).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Bar> deleteBar(@PathVariable("id") Integer id) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        Optional<User> optionalUser = this.userService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var bar = barService.findBarById(id);
        if (bar == null) {
            return ResponseEntity.notFound().build();
        }
        if (!bar.getOwner().getUsername().equals(username)) {
            return ResponseEntity.badRequest().build();
        }
        barService.removeBar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    public Map<String, BigDecimal> getBarsByCoordinates(String completeAddress) {
        try {
            String surl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(completeAddress, StandardCharsets.UTF_8) + "&key=AIzaSyANZc7ydpfQndh5qg-SWNHcBL9KwKh_jlA";
            var url = new URL(surl);
            var is = url.openConnection().getInputStream();

            var streamReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            var responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            var jo = new JSONObject(responseStrBuilder.toString());
            var results = jo.getJSONArray("results");
            Map<String, BigDecimal> ret = new HashMap<>();
            if (results.length() > 0) {
                JSONObject jsonObject;
                jsonObject = results.getJSONObject(0);
                ret.put("lat", jsonObject.getJSONObject("geometry").getJSONObject("location").getBigDecimal("lat"));
                ret.put("lng", jsonObject.getJSONObject("geometry").getJSONObject("location").getBigDecimal("lng"));
                return ret;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Double getDistance(Double barLat, Double barLng, Double userLat, Double userLng) {

        double theta = barLng - userLng;
        double dist = Math.sin(deg2rad(barLat)) * Math.sin(deg2rad(userLat)) + Math.cos(deg2rad(barLat)) * Math.cos(deg2rad(userLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344 * 1000;
        return (dist);
    }


    //This function converts decimal degrees to radians           
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    //This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @PostMapping("/capacity")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<BarCapacity>> getTablesAndCapacity(@Valid @RequestBody Map<String, String> location) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        List<Bar> bares = getBarsByUser();
        List<BarCapacity> res = new ArrayList<>();

        for (Bar b : bares) {
            if (!b.isSubscriptionActive() && authorities.contains(ROLE_CLIENT)) continue;

            Pair<Integer, Integer> freeAndDisabledTables = getFreeAndDisabledTables(b);
            var numeroMesasLibres = freeAndDisabledTables.getFirst();
            var disabled = freeAndDisabledTables.getSecond();

            String capacity = numeroMesasLibres + "/" + (b.getBarTables().size() - disabled);
            var barCapacity = new BarCapacity();
            Map<String, BigDecimal> coords = getBarsByCoordinates(b.getLocation());

            if (!(location.get("lat") == null || location.get("lng") == null) && coords != null) {
                Double distance = getDistance(coords.get("lat").doubleValue(), coords.get("lng").doubleValue(),
                        Double.valueOf(location.get("lat")), Double.valueOf(location.get("lng")));
                barCapacity.setDistance(distance);
            } else if(!(location.get("lat") == null || location.get("lng") == null)) {
                barCapacity.setDistance(null);
            }

            barCapacity.setId(b.getId());
            barCapacity.setName(b.getName());
            barCapacity.setLocation(b.getLocation());
            barCapacity.setCapacity(capacity);
            barCapacity.setCoord(coords);

            res.add(barCapacity);
        }

        if (!(location.get("lat") == null || location.get("lng") == null) && authorities.contains(ROLE_CLIENT)) {
            res = res.stream()
                    .filter(x -> Integer.parseInt(x.getCapacity().split("/")[0]) > 0).filter(y -> y.getDistance() != null)
                    .sorted(Comparator.comparingDouble(BarCapacity::getDistance)).collect(Collectors.toList());
        } else if (authorities.contains(ROLE_CLIENT)) {
            res = res.stream()
                    .filter(x -> Integer.parseInt(x.getCapacity().split("/")[0]) > 0)
                    .collect(Collectors.toList());
            res.sort(Comparator.comparing(i -> Integer.valueOf(i.getCapacity().split("/")[0])));
        }

        if (res.size() > 15) {
            res.subList(0, 15);
        }
        return ResponseEntity.ok(res);
    }

    private List<Bar> getBarsByUser() {
        List<Bar> bares;
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        if (authorities.contains(ROLE_OWNER)) {
            bares = barService.findAllBarByOwner(userService.getByUsername(ud.getUsername()));
        } else if (authorities.contains(ROLE_EMPLOYEE)) {
            bares = barService.findAllBarByEmployee(userService.getByUsername(ud.getUsername()));
        } else {
            bares = barService.findAllBar();
        }

        return bares;
    }

    private void computeBarData(Bar b, Map<String, String> location, List<BarCapacity> res) {
        Map<String, BigDecimal> coords = getBarsByCoordinates(b.getLocation());
        if (b.isSubscriptionActive() && coords != null) {

            Pair<Integer, Integer> freeAndDisabledTables = getFreeAndDisabledTables(b);
            var numeroMesasLibres = freeAndDisabledTables.getFirst();
            var disabled = freeAndDisabledTables.getSecond();

            String capacity = numeroMesasLibres + "/" + (b.getBarTables().size() - disabled);
            Double distance = getDistance(coords.get("lat").doubleValue(), coords.get("lng").doubleValue(),
                    Double.valueOf(location.get("lat")), Double.valueOf(location.get("lng")));

            if (distance <= 10000.) {
                var barCapacity = new BarCapacity();

                barCapacity.setDistance(distance);
                barCapacity.setId(b.getId());
                barCapacity.setName(b.getName());
                barCapacity.setLocation(b.getLocation());
                barCapacity.setCapacity(capacity);
                barCapacity.setCoord(coords);

                res.add(barCapacity);
            }
        }
    }


    @PostMapping("/map")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<BarCapacity>> getBarsByLocation(@Valid @RequestBody Map<String, String> location) {
        List<Bar> bares = getBarsByUser();
        List<BarCapacity> res = new ArrayList<>();

        if (!(location.get("lat") == null || location.get("lng") == null)) {
            for (Bar b : bares) {
                computeBarData(b, location, res);
            }

            res = res.stream()
                    .filter(x -> Integer.parseInt(x.getCapacity().split("/")[0]) > 0)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(res);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<BarDTO> getBarById(@PathVariable("id") Integer id) {
        var bar = barService.findBarById(id);
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();

        if (bar != null) {
            // Check if bar subscription is active otherwise return payment required response (HTTP 402)
            if (!bar.isSubscriptionActive() && bar.getOwner().getUsername().equals(username)) {
                return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
            } else if (!bar.isSubscriptionActive()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Pair<Integer, Integer> freeAndDisabledTables = getFreeAndDisabledTables(bar);
            var freeTables = freeAndDisabledTables.getFirst();
            var disabled = freeAndDisabledTables.getSecond();

            var reviews = orderReviews(bar.getReviews());

            var barDTO = new BarDTO(bar.getId(), bar.getName(), bar.getDescription(), bar.getContact(),
                    bar.getLocation(), bar.getOpeningTime(), bar.getClosingTime(), bar.getImages(),
                    bar.getBarTables().size() - disabled, freeTables, bar.getOwner().getUsername(), bar.getEmployees(),reviews);
            return ResponseEntity.ok(barDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private List<Review> orderReviews(Set<Review> reviews) {
        Set<Review> res = new HashSet<>(reviews);
        return  res.stream()
                .sorted(Comparator.comparing(Review::getCreationDate).thenComparing(Review::getValue).reversed().thenComparing(Review::getId))
                .collect(Collectors.toList());

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BarCreateDTO> updateBar(@Valid @RequestBody BarCreateDTO barDTO, @PathVariable("id") Integer id) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        Optional<User> optionalUser = this.userService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var updatedBar = this.barService.findBarById(id);
        if (updatedBar == null) {
            return ResponseEntity.notFound().build();
        }
        if (!updatedBar.isSubscriptionActive()) {
            return ResponseEntity.badRequest().build();
        }
        if (!username.equals(updatedBar.getOwner().getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        updatedBar.setName(barDTO.getName());
        updatedBar.setDescription(barDTO.getDescription());
        updatedBar.setContact(barDTO.getContact());
        updatedBar.setLocation(barDTO.getLocation());
        updatedBar.setOpeningTime(barDTO.getOpeningTime());
        updatedBar.setClosingTime(barDTO.getClosingTime());
        if (!barDTO.getImages().isEmpty()) {
            updatedBar.addImages(barDTO.getImages());
        }

        this.barService.createBar(updatedBar);
        return ResponseEntity.ok(barDTO);
    }

    @DeleteMapping("/{id}/image/{imageId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Bar> deleteImage(@PathVariable("id") Integer id, @PathVariable("imageId") Integer imageId) {
        UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ud.getUsername();
        Optional<User> optionalUser = this.userService.getUserByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var bar = barService.findBarById(id);
        if (bar == null || bar.getImages().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!bar.isSubscriptionActive()) {
            return ResponseEntity.badRequest().build();
        }
        if (!username.equals(bar.getOwner().getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Set<DBImage> images = bar.getImages();
        DBImage imageToDelete = this.dbImageService.getimageById(imageId);
        if (imageToDelete == null) {
            return ResponseEntity.notFound().build();
        }
        if (!images.contains(imageToDelete)) {
            return ResponseEntity.badRequest().build();
        }
        bar.deleteImage(imageToDelete);
        barService.createBar(bar);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/search/{text}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('OWNER') or hasRole('EMPLOYEE') ")
    public ResponseEntity<List<BarCapacity>> getBarsBySearch(@PathVariable("text") String text, @Valid @RequestBody Map<String, String> location) {
        List<Bar> barsSearch = this.barService.getBarsBySearch(text);
        List<BarCapacity> listBarSearch = new ArrayList<>();
        for (Bar b : barsSearch) {
            if (b.isSubscriptionActive()) {
                var barCapacity = new BarCapacity();
                barCapacity.setId(b.getId());
                barCapacity.setLocation(b.getLocation());
                barCapacity.setName(b.getName());

                Pair<Integer, Integer> freeAndDisabledTables = getFreeAndDisabledTables(b);
                var numeroMesasLibres = freeAndDisabledTables.getFirst();
                var disabled = freeAndDisabledTables.getSecond();

                barCapacity.setCapacity(numeroMesasLibres + "/" + (b.getBarTables().size() - disabled));
                Map<String, BigDecimal> coords = getBarsByCoordinates(b.getLocation());
                if (!(location.get("lat") == null || location.get("lng") == null)) {
                    if (coords != null) {
                        Double distance = getDistance(coords.get("lat").doubleValue(), coords.get("lng").doubleValue(),
                                Double.valueOf(location.get("lat")), Double.valueOf(location.get("lng")));
                        barCapacity.setDistance(distance);
                    } else {
                        barCapacity.setDistance(null);
                    }
                }

                barCapacity.setCoord(coords);

                listBarSearch.add(barCapacity);
            }
        }
        return new ResponseEntity<>(listBarSearch, HttpStatus.OK);
    }

    private Pair<Integer, Integer> getFreeAndDisabledTables(Bar b) {
        var numeroMesasLibres = 0;
        var disabled = 0;
        for (BarTable bt : b.getBarTables()) {
            if (bt.isFree() && bt.isAvailable()) {
                numeroMesasLibres += 1;
            }
            if (!bt.isAvailable()) {
                disabled++;
            }
        }
        return Pair.of(numeroMesasLibres, disabled);
    }


    @GetMapping("barClient/{username}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Bar> getBarForClient(@PathVariable("username") final String username) {
        var client = this.clientService.getClientByUsername(username);
        if (client != null) {
            BarTable table = client.getTable();
            if (table != null) {
                return new ResponseEntity<>(table.getBar(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}