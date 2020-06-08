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

const DEFAULT_MAX_COMMENT_NUM=1

/**
 * Adds a random description to the page.
 */
function addRandomDescription() {
  const descriptions = ["Noogler!", "Dog Girl", "Programmer", "Food Lover"];

  // Pick a random description.
  const description =
    descriptions[Math.floor(Math.random() * descriptions.length)];

  // Add it to the page.
  const descriptionContainer = document.getElementById("description-container");
  descriptionContainer.innerText = description;
}

function getCommentInForm() {
  inputVal = document.getElementById("quantity").value;
  var numComments;
  if (inputVal != '') { 
      numComments = inputVal;
  } else {
      numComments = DEFAULT_MAX_COMMENT_NUM;
  }
  var url = "/data?numComment=" + numComments;
  fetch(url).then(response => response.text()).then((quote) => {
    document.getElementById('comment-container').innerText = quote;
  });
}

function deleteAllComments() {
  const params = new URLSearchParams();
  fetch("/delete-data", { method: "POST", body: params });
}

/**
 * The function is to fetch a Promise from "/userLogIn" page, convert the logIn status message
 * into text and add them into the fetch-container displayed on the webpage.
 */
function getUserLogInStatus() {
  fetch("/userLogIn")
    .then((response) =>response.json())
    .then((quote) => {
      if (quote.status == 1) {
          document.getElementById("form-blk").style.display = "block";
          document.getElementById("userInfo-container").innerHTML = 
              "<p> Hello " + quote.email + "! <br> You have already logged in. </p> <p>Logout <a href=\"" 
              + quote.logoutUrl + "\">here</a>.</p>"
      } else {
          document.getElementById("form-blk").style.display = "none";
          document.getElementById("userInfo-container").innerHTML = 
            "<p> Hello Stranger</p> <p>Please <a href=\"" + quote.loginUrl + "\">login</a> first.</p>"
      }
    });

}
