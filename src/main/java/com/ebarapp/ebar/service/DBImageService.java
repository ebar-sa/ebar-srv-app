package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.DBImage;
import com.ebarapp.ebar.repository.DBImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBImageService {

    @Autowired
    private DBImageRepository dbImageRepository;

    public DBImage getimageById(Integer imageId) { return this.dbImageRepository.getDBImageById(imageId);}
}
