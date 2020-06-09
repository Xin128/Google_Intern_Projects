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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
  * Servlet that returns comments in database. 
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  protected static final String COMMENT = "Comment";
  private final String CONTENT_PROPERTY = "content";
  private final int DEFAULT_MAX_COMMENT_NUM = 1;
  private final String INPUT_MSG_FORM = "comment-input";
  private final String NUM_COMMENT_FORM = "numComment";
  private final String TIMESTAMP_PROPERTY = "timestamp";
  private final String USEREMAIL_PROPERTY = "userEmail";


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the limiting number of comments
    String requestedNumComment = request.getParameter(NUM_COMMENT_FORM);
    if (requestedNumComment == null) {
        return; 
    }
    int maxNumComments = Integer.parseInt(requestedNumComment);
    
    // Create the query and prepared query to load comment entities from database
    Query query = new Query(COMMENT).addSort(TIMESTAMP_PROPERTY, SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Add limited number of comment contents and user email to the userComment Map  
    HashMap<String, ArrayList<String>> userComment = new HashMap<String, ArrayList<String>>();
    for (Entity commentEntity : results.asList(FetchOptions.Builder.withLimit(maxNumComments))) {
        String commentMsg = (String) commentEntity.getProperty(CONTENT_PROPERTY);
        String commentUser = (String) commentEntity.getProperty(USEREMAIL_PROPERTY);
        ArrayList<String> msglist = userComment.get(commentUser);
        if (msglist == null) {
            // msglist = new ArrayList<String>();
            // msglist.add(commentMsg);
            userComment.put(commentUser, new ArrayList<String>(Arrays.asList(commentMsg)));
        } else {
            msglist.add(commentMsg);
        }
    }
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(userComment));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String inputMsg = request.getParameter(INPUT_MSG_FORM);
    if (inputMsg == null) {
        return; 
    }
    
    if (!inputMsg.isEmpty()) {
      // Create an entity with received comment message
      long timestamp = System.currentTimeMillis();
      Entity commentEntity = new Entity(COMMENT);
      UserService userService = UserServiceFactory.getUserService();      
      String userEmail = userService.getCurrentUser().getEmail();
      commentEntity.setProperty(CONTENT_PROPERTY, inputMsg);
      commentEntity.setProperty(TIMESTAMP_PROPERTY,timestamp);
      commentEntity.setProperty(USEREMAIL_PROPERTY, userEmail);

    // Used Datastore survice to store newly created comment entity
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
    }
    // Redirect back to the current page
    response.sendRedirect("/index.html");
  }

}
