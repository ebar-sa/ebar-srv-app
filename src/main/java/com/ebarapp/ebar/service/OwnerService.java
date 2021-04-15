package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Owner;
import com.ebarapp.ebar.repository.OwnerRepository;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private OwnerRepository ownerRepository;

    public void saveOwner(Owner newOwner) { this.ownerRepository.save(newOwner); }
}
