/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.viewservices.myprofile.api;

import org.odpi.openmetadata.frameworks.openmetadata.metadataelements.ToDoElement;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.openmetadata.enums.ToDoStatus;

import java.util.List;

/**
 * MyRolesAndActionsInterface covers the ability to query a person's roles and any actions (to dos) assigned
 * to those roles.
 */
public interface MyRolesAndActionsInterface
{
    /**
     * Return a list of to dos assigned to the calling user.
     *
     * @param userId     userId of user making request.
     * @param toDoStatus     status of the to do (null means current active)
     * @param startFrom  index of the list to start from (0 for start)
     * @param pageSize   maximum number of elements to return.
     *
     * @return list of to do details
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    List<ToDoElement> getMyToDos(String     userId,
                                 ToDoStatus toDoStatus,
                                 int        startFrom,
                                 int        pageSize) throws InvalidParameterException,
                                                             PropertyServerException,
                                                             UserNotAuthorizedException;


    /**
     * Return a list of to dos assigned to the calling user.
     *
     * @param userId           userId of user making request.
     * @param actorGUID unique identifier of the user's actor.
     * @param status           status of the to do (null means current active)
     * @param startFrom        index of the list to start from (0 for start)
     * @param pageSize         maximum number of elements to return.
     *
     * @return list of to do details
     *
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws PropertyServerException there is a problem retrieving information from the property server(s).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    List<ToDoElement> getMyToDosByActor(String     userId,
                                        String     actorGUID,
                                        ToDoStatus status,
                                        int        startFrom,
                                        int        pageSize) throws InvalidParameterException,
                                                                    PropertyServerException,
                                                                    UserNotAuthorizedException;
}
