/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server.data;

import java.lang.reflect.Method;
import java.util.EventObject;

import com.vaadin.event.EventRouter;
import com.vaadin.shared.Registration;

/**
 * Abstract data provider implementation which takes care of refreshing data
 * from the underlying data provider.
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public abstract class AbstractDataProvider<T, F> implements DataProvider<T, F> {

    private EventRouter eventRouter;

    @Override
    public Registration addDataProviderListener(DataProviderListener listener) {
        addListener(DataChangeEvent.class, listener,
                DataProviderListener.class.getMethods()[0]);
        return () -> removeListener(DataChangeEvent.class, listener);
    }

    @Override
    public void refreshAll() {
        fireEvent(new DataChangeEvent(this));
    }

    /**
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     *
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param listener
     *            the object instance who owns the activation method.
     * @param method
     *            the activation method.
     *
     */
    protected void addListener(Class<?> eventType,
            DataProviderListener listener, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, listener, method);
    }

    /**
     * Removes all registered listeners matching the given parameters. Since
     * this method receives the event type and the listener object as
     * parameters, it will unregister all <code>object</code>'s methods that are
     * registered to listen to events of type <code>eventType</code> generated
     * by this component.
     *
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param listener
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     */
    protected void removeListener(Class<?> eventType,
            DataProviderListener listener) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, listener);
        }
    }

    /**
     * Sends the event to all listeners.
     *
     * @param event
     *            the Event to be sent to all listeners.
     */
    protected void fireEvent(EventObject event) {
        if (eventRouter != null) {
            eventRouter.fireEvent(event);
        }
    }
}
