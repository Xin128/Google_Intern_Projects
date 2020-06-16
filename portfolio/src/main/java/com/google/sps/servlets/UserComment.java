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

import java.util.ArrayList;
import org.javatuples.Pair;

public class UserComment {
  private ArrayList<Pair<String, String>> commentList = new ArrayList<>();
  private String userName;

  public UserComment(String userName, String commentMsg, String imagUrl) {
    System.out.println("userComment get called?");
    this.setuserName(userName);
    this.addCommentEntity(commentMsg,imagUrl);
  }
  public String getuserName() {
    return userName;
  }

  public ArrayList<Pair<String, String>> getCommentList() {
    return commentList;
  }

  public void addCommentEntity(String commentMsg, String imageUrl) {
    Pair<String, String> commentEntity = new Pair<>(commentMsg,imageUrl);
    this.commentList.add(commentEntity);
  }

  public void setuserName(String userName) {
    this.userName = userName;
  }
}
