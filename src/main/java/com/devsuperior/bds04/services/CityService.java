package com.devsuperior.bds04.services;

import com.devsuperior.bds04.dto.CityDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.repositories.CityRepository;
import com.devsuperior.bds04.services.exceptions.DataNotFoundException;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    @Autowired
    private CityRepository repository;

    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {
        return repository.findAll(Sort.by("name")).stream().map(CityDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public CityDTO save(CityDTO city) {
        return new CityDTO(repository.save(new City(city.getId(), city.getName())));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteById(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Cidade não encontrada id: " + id + " entity: " + City.class.getName());
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Impossivel excluir. Possui entidades relacionadas id: " + id + " entity: " + City.class.getName());
        }
    }

}
