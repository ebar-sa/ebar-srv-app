import http from "../http-common";

class BarDataService {
    getAll(){
        return http.get("/bares");
    }
}

export default new BarDataService();