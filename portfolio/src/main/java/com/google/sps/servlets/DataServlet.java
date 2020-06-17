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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
  * Servlet that returns comments in database. 
  * Note: We first added the Gson library dependency to pom.xml.
  */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  protected static final String BLOB_URL_PROPERTY = "blob_url";
  protected static final String COMMENT = "Comment";
  protected static final String CONTENT_PROPERTY = "content";
  protected static final String TIMESTAMP_PROPERTY = "timestamp";
  private final String DEFAULT_USERNAME = "defaultUser";
  private final String NUM_COMMENT_FORM = "numComment";
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

    /* Add limited number of comment entity  and user email to the userList Map
     * Note: userList data structure:
     * {UserName1: [User1Comment1, User1Comment2...],
     * UserName2: [User2Comment1, User2Comment2...],...}
     *      */
    HashMap<String, ArrayList<UserComment>> userList = new HashMap();
    for (Entity commentEntity : results.asList(FetchOptions.Builder.withLimit(maxNumComments))) {
      // Get the different properties (userEmail, message, image) of a comment Entity
      String commentMsg = (String) commentEntity.getProperty(CONTENT_PROPERTY);
      String commentUser = (String) commentEntity.getProperty(USEREMAIL_PROPERTY);
      if (commentUser == null) {
        commentUser = DEFAULT_USERNAME;
      }
      String imageUrl = (String) commentEntity.getProperty(BLOB_URL_PROPERTY);
      ArrayList<UserComment> msglist = userList.get(commentUser);
      UserComment newComment = new UserComment(commentUser, commentMsg, imageUrl);
      if (msglist == null) {
        userList.put(commentUser, new ArrayList<>(Arrays.asList(newComment)));
      } else {
        msglist.add(newComment);
      }
    }
    System.out.println(new Gson().toJson(userList));
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(userList));
  }

}
