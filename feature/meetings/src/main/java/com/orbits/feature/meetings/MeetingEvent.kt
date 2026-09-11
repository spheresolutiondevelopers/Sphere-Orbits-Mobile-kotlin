package com.orbits.feature.meetings

import com.orbits.domain.meetings.Meeting

/**
 * UI events for the meetings feature.
 */
sealed class MeetingEvent {
    data object LoadMeetings : MeetingEvent()
    data object Refresh : MeetingEvent()
    data class SelectFilter(val filter: MeetingFilter) : MeetingEvent()
    data class SelectMeeting(val meetingId: String) : MeetingEvent()
    data class NavigateToMeetingDetail(val meetingId: String) : MeetingEvent()
    data object NavigateToCreate : MeetingEvent()
    data object DismissError : MeetingEvent()
    data class JoinMeeting(val meetingId: String) : MeetingEvent()
    data class UpdateMeetingStatus(val meetingId: String, val status: String) : MeetingEvent()
}
