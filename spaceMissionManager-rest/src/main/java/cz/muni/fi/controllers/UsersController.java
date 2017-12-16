package cz.muni.fi.controllers;


import cz.muni.fi.ApiUris;
import cz.muni.fi.dto.UserCreateDTO;
import cz.muni.fi.dto.UserDTO;
import cz.muni.fi.exceptions.ResourceAlreadyExistsException;
import cz.muni.fi.exceptions.ResourceNotFoundException;
import cz.muni.fi.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

/**
 * Created by jsmadis
 *
 * @author jsmadis
 */

@RestController
@RequestMapping(ApiUris.ROOT_URI_USERS)
public class UsersController {

    private final static Logger logger = LoggerFactory.getLogger(UsersController.class);


    @Autowired
    private UserFacade userFacade;

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserDTO> getUsers() {
        logger.debug("[REST] getUsers()");
        return userFacade.findAllUsers();

    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUser(@PathVariable("id") long id) {
        logger.debug("[REST] getUser() id=" + id);
        UserDTO userDTO = userFacade.findUserById(id);
        if (userDTO == null) {
            throw new ResourceNotFoundException();
        }
        return userDTO;
    }

    @RolesAllowed("ROLE_MANAGER")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Long createUser(@RequestBody UserCreateDTO user) {

        logger.debug("[REST] createUser()");

        try {
            return userFacade.addUser(user);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new ResourceAlreadyExistsException();
        }
    }

    @RolesAllowed("ROLE_MANAGER")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Collection<UserDTO> deleteUser(@PathVariable Long id) {
        logger.debug("[REST] deleteUser()");

        UserDTO user = userFacade.findUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        userFacade.deleteUser(user);
        return userFacade.findAllUsers();
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(value = "/astronauts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserDTO> getAstronauts() {
        logger.debug("[REST] getAstronauts()");

        return userFacade.findAllAstronauts();
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(value = "/astronauts/available", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserDTO> getAvailableAstronauts() {
        logger.debug("[REST] getAvailableAstronauts()");

        return userFacade.findAllAvailableAstronauts();
    }

    @RolesAllowed("ROLE_MANAGER")
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO updateUser(@RequestBody UserDTO user) {
        logger.debug("[REST] updateUser()");

        try {
            userFacade.updateUser(user);
            return user;
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new ResourceAlreadyExistsException();
        }
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(value = "/{id}/acceptMission", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO acceptMission(@PathVariable Long id) {
        logger.debug("[REST] acceptMission()");

        UserDTO user = userFacade.findUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        userFacade.acceptAssignedMission(user);
        return user;
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_USER"})
    @RequestMapping(value = "{id}/rejectMission", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO rejectMission(@PathVariable Long id, @RequestBody String explanation) {
        logger.debug("[REST] rejectMission");

        UserDTO user = userFacade.findUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        userFacade.rejectAssignedMission(user, explanation);
        return user;
    }


}
