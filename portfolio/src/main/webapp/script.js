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
  var commentContainer = document.getElementById('comment-container');
  fetch(url).then(response => response.json()).
    then((commentArray) => commentArray.forEach(comment => {
        fetch(comment[1]).then(response => response.blob()).then((bloburl) => {
            var objectURL = URL.createObjectURL(bloburl);
            var imgElem = document.createElement('img');
            imgElem.src = objectURL;
            commentContainer.append(imgElem);
        })
  }));
}

function deleteAllComments() {
  const params = new URLSearchParams();
  fetch("/delete-data", { method: "POST", body: params });
}

function fetchBlobstoreUrlAndShowForm() {
  const messageForm = document.getElementById('my-form');
  var url = "/data?blob_upload_url=" + messageForm;
  fetch(url)
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
      });
}
