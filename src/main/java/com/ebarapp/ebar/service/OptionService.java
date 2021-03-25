package com.ebarapp.ebar.service;

import com.ebarapp.ebar.model.Option;
import com.ebarapp.ebar.model.Voting;
import com.ebarapp.ebar.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    public Option createOption(Integer votingId, Option newOption) {
        return optionRepository.save(newOption);
    }

    public void removeOption(Integer id) {
        optionRepository.deleteById(id);
    }

    public Option getOptionById(Integer id) {
        Optional<Option> option = optionRepository.findById(id);
        Option res = null;
        if (option.isPresent()){
            res = option.get();
        }
        return res;
    }

}
