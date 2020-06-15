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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
  * Servlet that ensure User's log in status.
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/userLogIn")
public class UserLoginServelet extends HttpServlet {
  final private String redirectStr = "/";
  private HashMap<String, Object> userInfo = new HashMap<String, Object>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String logoutUrl = userService.createLogoutURL(redirectStr);
      userInfo.put("status", 1);
      userInfo.put("email", userEmail);
      userInfo.put("logoutUrl", logoutUrl);
    } else {
      String loginUrl = userService.createLoginURL(redirectStr);
      userInfo.put("status", 0);
      userInfo.put("loginUrl",loginUrl);
    }
    response.getWriter().println(new Gson().toJson(userInfo));
  }

}