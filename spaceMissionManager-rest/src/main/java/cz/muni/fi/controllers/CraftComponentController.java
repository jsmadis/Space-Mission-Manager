package cz.muni.fi.controllers;


import cz.muni.fi.ApiUris;
import cz.muni.fi.dto.CraftComponentCreateDTO;
import cz.muni.fi.dto.CraftComponentDTO;
import cz.muni.fi.dto.SpacecraftDTO;
import cz.muni.fi.exceptions.ResourceAlreadyExistsException;
import cz.muni.fi.exceptions.ResourceNotFoundException;
import cz.muni.fi.facade.CraftComponentFacade;
import cz.muni.fi.facade.SpacecraftFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiUris.ROOT_URI_CRAFTCOMPONENTS)
public class CraftComponentController {

	private final static Logger logger = Logger.getLogger(CraftComponentController.class.getName());


	private CraftComponentFacade craftComponentFacade;

	@Autowired
	private SpacecraftFacade spacecraftFacade;

	@Autowired
	public CraftComponentController(CraftComponentFacade craftComponentFacade) {
		this.craftComponentFacade = craftComponentFacade;
	}


	@RolesAllowed("ROLE_MANAGER")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public CraftComponentDTO createCraftComponent(@RequestBody CraftComponentCreateDTO craftComponentCreateDTO) throws Exception {
		logger.log(Level.INFO, "[REST] Creating CraftComponent...");
		try {
			return craftComponentFacade.findComponentById(craftComponentFacade.addComponent(craftComponentCreateDTO));
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new ResourceAlreadyExistsException();
		}
	}

	@RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CraftComponentDTO> findAllComponents() {
		logger.log(Level.INFO, "[REST] finding all components...");
		return craftComponentFacade.findAllComponents();
	}

	@RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CraftComponentDTO findComponentById(@PathVariable("id") Long id) {
		logger.log(Level.INFO, "[REST] finding component " + id + "...");
		CraftComponentDTO componentDTO = craftComponentFacade.findComponentById(id);
		if (componentDTO == null) {
			throw new ResourceNotFoundException();
		}
		return componentDTO;
	}

	@RolesAllowed({"ROLE_MANAGER"})
	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CraftComponentDTO updateComponent(@RequestBody CraftComponentDTO craftComponentDTO) {
		logger.log(Level.INFO, "[REST] updating component" + craftComponentDTO.getId() + "...");
		CraftComponentDTO componentDTO = craftComponentFacade.findComponentById(craftComponentDTO.getId());
		if (componentDTO == null) {
			throw new ResourceNotFoundException();
		}
		try {
			craftComponentFacade.updateComponent(craftComponentDTO);
			return craftComponentFacade.findComponentById(componentDTO.getId());
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			throw new ResourceAlreadyExistsException();
		}
	}

	@RolesAllowed({"ROLE_MANAGER"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CraftComponentDTO> removeComponent(@PathVariable("id") long id) throws Exception {
		logger.log(Level.INFO, "[REST] deleting component" + id + "...");
		CraftComponentDTO component = craftComponentFacade.findComponentById(id);
		if (component == null) {
			throw new ResourceNotFoundException();
		}
		craftComponentFacade.removeComponent(component);
		return craftComponentFacade.findAllComponents();
	}


	@RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
	@RequestMapping(value = "/available", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CraftComponentDTO> findAllAvailableComponents() {
		logger.log(Level.INFO, "[REST] finding all available components...");
		List<SpacecraftDTO> spacecraftDTOs = spacecraftFacade.findAllSpacecrafts();
		List<CraftComponentDTO> cc = new ArrayList<>();
		for (SpacecraftDTO sc :spacecraftDTOs) {
			cc.addAll(sc.getComponents());
		}
		List<CraftComponentDTO> result = new ArrayList<>();
		for (CraftComponentDTO c : craftComponentFacade.findAllComponents()) {
			if(!cc.contains(c)){
				result.add(c);
			}
		}

		return result;

	}


}
