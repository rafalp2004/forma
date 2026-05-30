// ── Auth / User (właściciel: Arek) ──────────────────────────────────────────

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
}

export enum UserGoal {
  LOSE_WEIGHT = 'LOSE_WEIGHT',
  GAIN_WEIGHT = 'GAIN_WEIGHT',
  MAINTAIN_WEIGHT = 'MAINTAIN_WEIGHT',
}

export interface UserDto {
  id: number
  username: string
  email: string
}

export interface PersonalRecordDto {
  exerciseName: string
  weight: number
  date: string
}

export interface UserDetailsResponse {
  username: string
  email: string
  age: number | null
  weight: number | null
  height: number | null
  gender: Gender | null
  goal: UserGoal | null
  targetWeight: number | null
  sessionsPerWeek: number | null
  createdAt: string
  updatedAt: string
  workoutCount: number
  challengeCount: number
  personalRecords: PersonalRecordDto[]
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
}

// ── Workout (właściciel: Mateusz) ────────────────────────────────────────────

export interface ExerciseDto {
  id: string
  name: string
  muscleGroup: string
  equipment: string
  gifUrl: string
}

export interface WorkoutSetDto {
  exerciseId: string
  exerciseName: string
  muscleGroup: string
  reps: number
  weightKg: number
  performedAt: string
}

export interface WorkoutSummaryDto {
  id: number
  userId: number
  completedAt: string
  totalVolumeKg: number
  totalSets: number
  sets: WorkoutSetDto[]
}

// ── Planning (właściciel: Antoni) ────────────────────────────────────────────

export interface PlanDto {
  id: number
  userId: number
  name: string
  createdAt: string
}

// ── Social (właściciel: Rafał) ───────────────────────────────────────────────

export type ChallengeMetric = 'TOTAL_VOLUME' | 'WORKOUT_COUNT' | 'STREAK_DAYS'

export interface ChallengeDto {
  id: number
  name: string
  metric: ChallengeMetric
  startDate: string
  endDate: string
}

export interface LeaderboardEntryDto {
  userId: number
  username: string
  score: number
  rank: number
}

// ── Nutrition (właściciel: Oskar) ────────────────────────────────────────────

export interface ProductDto {
  id: number
  name: string
  kcalPer100g: number
  proteinPer100g: number
  carbsPer100g: number
  fatPer100g: number
}

export interface MealEntryDto {
  id: number
  productId: number
  productName: string
  grams: number
  kcal: number
  date: string
}
