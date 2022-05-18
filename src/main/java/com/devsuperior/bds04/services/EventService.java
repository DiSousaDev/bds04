package com.devsuperior.bds04.services;

import com.devsuperior.bds04.dto.EventDTO;
import com.devsuperior.bds04.entities.City;
import com.devsuperior.bds04.entities.Event;
import com.devsuperior.bds04.repositories.EventRepository;
import com.devsuperior.bds04.services.exceptions.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    @Autowired
    private EventRepository repository;

    @Transactional(readOnly = true)
    public Page<EventDTO> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(EventDTO::new);
    }

    @Transactional
    public EventDTO save(EventDTO eventDTO) {
        Event event = new Event();
        copyDtoToEntity(eventDTO, event);
        event = repository.saveAndFlush(event);
        return new EventDTO(event);
    }

    @Transactional
    public EventDTO update(Long id, EventDTO eventDTO) {
        Event event = repository.findById(id).orElseThrow(() -> new DataNotFoundException("Evento não encontrado."));
        copyDtoToEntity(eventDTO, event);
        event = repository.save(event);
        return new EventDTO(event);
    }

    private void copyDtoToEntity(EventDTO eventDTO, Event event) {
        event.setName(eventDTO.getName());
        event.setDate(eventDTO.getDate());
        event.setUrl(eventDTO.getUrl());
        event.setCity(new City(eventDTO.getCityId(), null));
    }

}
