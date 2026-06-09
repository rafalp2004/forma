import { apiClient } from '@/shared/api/client'
import type {
  ChallengeCreateDto,
  ChallengeDto,
  FeedCommentDto,
  FeedEntryDto,
  FriendDto,
  LeaderboardEntryDto,
  UserSearchDto,
} from './types'

export const socialApi = {
  getFriends(): Promise<FriendDto[]> {
    return apiClient.get<FriendDto[]>('/friends').then((r) => r.data)
  },

  getPendingRequests(): Promise<FriendDto[]> {
    return apiClient.get<FriendDto[]>('/friends/pending').then((r) => r.data)
  },

  sendFriendRequest(addresseeId: number): Promise<FriendDto> {
    return apiClient.post<FriendDto>('/friends/request', { addresseeId }).then((r) => r.data)
  },

  acceptFriendRequest(id: number): Promise<FriendDto> {
    return apiClient.post<FriendDto>(`/friends/accept/${id}`).then((r) => r.data)
  },

  rejectFriendRequest(id: number): Promise<void> {
    return apiClient.post(`/friends/reject/${id}`).then(() => undefined)
  },

  removeFriend(friendshipId: number): Promise<void> {
    return apiClient.delete(`/friends/${friendshipId}`).then(() => undefined)
  },

  searchUsers(query: string): Promise<UserSearchDto[]> {
    return apiClient.get<UserSearchDto[]>('/users/search', { params: { query } }).then((r) => r.data)
  },

  getFeed(): Promise<FeedEntryDto[]> {
    return apiClient.get<FeedEntryDto[]>('/feed').then((r) => r.data)
  },

  addComment(feedEntryId: number, content: string): Promise<FeedCommentDto> {
    return apiClient.post<FeedCommentDto>(`/feed/${feedEntryId}/comments`, { content }).then((r) => r.data)
  },

  deleteComment(commentId: number): Promise<void> {
    return apiClient.delete(`/feed/comments/${commentId}`).then(() => undefined)
  },

  getActiveChallenges(): Promise<ChallengeDto[]> {
    return apiClient.get<ChallengeDto[]>('/challenges').then((r) => r.data)
  },

  getChallengeById(id: number): Promise<ChallengeDto> {
    return apiClient.get<ChallengeDto>(`/challenges/${id}`).then((r) => r.data)
  },

  createChallenge(dto: ChallengeCreateDto): Promise<ChallengeDto> {
    return apiClient.post<ChallengeDto>('/challenges', dto).then((r) => r.data)
  },

  joinChallenge(id: number): Promise<ChallengeDto> {
    return apiClient.post<ChallengeDto>(`/challenges/${id}/join`).then((r) => r.data)
  },

  leaveChallenge(id: number): Promise<void> {
    return apiClient.delete(`/challenges/${id}/leave`).then(() => undefined)
  },

  getLeaderboard(id: number): Promise<LeaderboardEntryDto[]> {
    return apiClient.get<LeaderboardEntryDto[]>(`/challenges/${id}/leaderboard`).then((r) => r.data)
  },
}
