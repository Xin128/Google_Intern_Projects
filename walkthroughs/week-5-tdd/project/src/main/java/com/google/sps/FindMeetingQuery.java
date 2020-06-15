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

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {
  private ArrayList<TimeRange> remainingTime;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> requiredAttendees = request.getAttendees();
    long requiredDuration = request.getDuration();
    if (requiredDuration > TimeRange.WHOLE_DAY.duration()) {
      return new ArrayList<>();
    }
    remainingTime = new ArrayList<>
                        (Collections.singletonList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)));
    for (Event event : events) {
      TimeRange eventTime = event.getWhen();
      if (!Collections.disjoint(event.getAttendees(), requiredAttendees)) {
          splitToTwoRange(eventTime, requiredDuration);
      }
    }
    return remainingTime;
  }

  private void splitToTwoRange(TimeRange occupied, long duration) {
    Collections.sort(remainingTime, TimeRange.ORDER_BY_END);
    int i = 0;
    while (i < remainingTime.size()) {
      if (remainingTime.get(i).overlaps(occupied)) {
        TimeRange prev = TimeRange.fromStartEnd(remainingTime.get(i).start(), occupied.start(), false);
        TimeRange after = TimeRange.fromStartEnd(occupied.end(), remainingTime.get(i).end(), false);
        remainingTime.remove(i);
        i -= 1;
        if (prev.duration() >= duration) {
          i += 1;
          remainingTime.add(i, prev);
        }
        if (after.duration() >= duration) {
          i += 1;
          remainingTime.add(i, after);
        }
      }
      i += 1;
    }
  }
}