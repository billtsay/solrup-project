/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.solrup.yitian.authz;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.osgi.framework.BundleContext;

@Command(scope = "yitian", name = "login", description = "Login Yitian")
@Service
public class LoginCommand implements Action {

    @Argument(index = 0, name = "username", description = "User name argument", required = false, multiValued = false)
    String username = null;

    @Argument(index = 1, name = "password", description = "Password argument", required = false, multiValued = false)
    @Completion(PasswordCompleter.class)
    String password = null;
    
    @Reference
    BundleContext bundleContext;

    @Reference
    Session session;

    @Override
    public Object execute() throws Exception {
        System.out.println(session.currentDir());
        return null;
    }
}
