// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
  * Servlet that ensure User's log in status.
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/userLogIn")
public class UserLogInServelet extends HttpServlet {
  Map<String,Object> userInfo = new HashMap<String, Object>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
      userInfo.put("status", 1);
      userInfo.put("email", userEmail);
      userInfo.put("logoutUrl", logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      userInfo.put("status", 0);
      userInfo.put("loginUrl",loginUrl);
    }
    response.getWriter().println(new Gson().toJson(userInfo));
  }

//   @Override
//   public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//     UserService userService = UserServiceFactory.getUserService();

//     // Only logged-in users can post messages
//     if (!userService.isUserLoggedIn()) {
//       response.sendRedirect("/userLogIn");
//       return;
//     }

//     String text = request.getParameter("text");
//     String email = userService.getCurrentUser().getEmail();

//     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//     Entity messageEntity = new Entity("Message");
//     messageEntity.setProperty("text", text);
//     messageEntity.setProperty("timestamp", System.currentTimeMillis());
//     datastore.put(messageEntity);

//     // Redirect to /userLogIn. The request will be routed to the doGet() function above.
//     response.sendRedirect("/userLogIn");
//   }

}