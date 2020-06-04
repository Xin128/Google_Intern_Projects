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
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


/**
  * Servlet that returns some example content. 
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  String comment = "Comment";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Create the query and prepared query to load comment entities from database
    Query query = new Query(comment);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    // Add all comment contents to the msgList  
    ArrayList<String> msglist = new ArrayList<String>();
    for (Entity comment:results.asIterable()) {
      String commentMsg = (String)comment.getProperty("content");
      msglist.add(commentMsg);
    }
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(msglist));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String inputMsg = request.getParameter("comment-input");
    if (!inputMsg.isEmpty()) {
        msglist.add(inputMsg);
    }

    // Create an entity with received comment message
    Entity commentEntity = new Entity(comment);
    String contentProperty = "content";
    commentEntity.setProperty(contentProperty, inputMsg);

    // Used Datastore survice to store newly created comment entity
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the current page
    response.sendRedirect("/index.html");
  }

    response.sendRedirect("/index.html");
  }

}
