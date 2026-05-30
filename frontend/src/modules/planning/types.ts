export interface ExerciseDto {
  id: string
  name: string
  muscleGroup: string
  equipment: string
  gifUrl: string
  instructions?: string
}

export interface PlanExerciseRequest {
  exerciseId: string
  exerciseName: string
  dayOfWeek: number
  sets: number
  reps: number
  targetWeightKg: number | null
}

export interface PlanExerciseResponse extends PlanExerciseRequest {
  id: number
}

export interface TrainingPlanRequest {
  name: string
  description: string | null
  startDate: string | null
  endDate: string | null
  exercises: PlanExerciseRequest[]
}

export interface TrainingPlanResponse {
  id: number
  userId: number
  name: string
  description: string | null
  status: string
  startDate: string | null
  endDate: string | null
  createdAt: string
  exercises: PlanExerciseResponse[]
}

export interface WeightEntryRequest {
  userId: number | null
  date: string
  weightKg: number
}

export interface WeightProgressPointResponse {
  date: string
  weightKg: number
}

export interface StrengthProgressPointResponse {
  date: string
  exerciseName: string
  maxWeight: number
}

export interface CalendarWorkoutResponse {
  date: string
  workoutId: number
  workoutName: string
  completedAt: string
  totalVolumeKg: number
  totalSets: number
  sets: CalendarWorkoutSetResponse[]
}

export interface CalendarWorkoutSetResponse {
  exerciseId: string
  exerciseName: string
  muscleGroup: string
  reps: number
  weightKg: number
  performedAt: string
}
