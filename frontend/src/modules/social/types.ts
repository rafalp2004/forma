export interface FriendDto {
  friendshipId: number
  userId: number
  username: string
  email: string
  status: string
}

export interface FriendRequestDto {
  addresseeId: number
}

export interface WorkoutSetSummary {
  exerciseName: string
  muscleGroup: string
  reps: number
  weightKg: number
}

export interface WorkoutSummary {
  id: number
  totalVolumeKg: number
  totalSets: number
  sets: WorkoutSetSummary[]
}

export interface FeedCommentDto {
  id: number
  feedEntryId: number
  userId: number
  username: string
  content: string
  createdAt: string
}

export interface FeedEntryDto {
  id: number
  userId: number
  username: string
  type: string
  startDate: string | null
  endDate: string | null
  createdAt: string
  challengeId: number | null
  challengeTitle: string | null
  workoutSummary: WorkoutSummary | null
  comments: FeedCommentDto[]
}

export interface ChallengeDto {
  id: number
  creatorId: number
  title: string
  description: string | null
  status: string
  startDate: string
  endDate: string
  metric: string
  createdAt: string
  participantCount: number
  isParticipant: boolean
}

export interface ChallengeCreateDto {
  title: string
  description: string
  startDate: string
  endDate: string
  metric: string
}

export interface LeaderboardEntryDto {
  rank: number
  userId: number
  username: string
  score: number
}

export interface UserSearchDto {
  id: number
  username: string
  email: string
}
