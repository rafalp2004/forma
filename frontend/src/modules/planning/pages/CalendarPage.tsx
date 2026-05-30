import { useEffect, useMemo, useState } from 'react'
import { Button, Card } from '@/shared/components'
import { planningApi } from '../api'
import type { CalendarWorkoutResponse } from '../types'

const dayLabels = ['Pon', 'Wt', 'Sr', 'Czw', 'Pt', 'Sob', 'Nd']
const monthNames = [
  'Styczen',
  'Luty',
  'Marzec',
  'Kwiecien',
  'Maj',
  'Czerwiec',
  'Lipiec',
  'Sierpien',
  'Wrzesien',
  'Pazdziernik',
  'Listopad',
  'Grudzien',
]

const dateFormatter = new Intl.DateTimeFormat('pl-PL', {
  weekday: 'long',
  day: '2-digit',
  month: 'long',
  year: 'numeric',
})

const timeFormatter = new Intl.DateTimeFormat('pl-PL', {
  hour: '2-digit',
  minute: '2-digit',
})

interface CalendarDay {
  date: string
  dayNumber: number
  inCurrentMonth: boolean
}

function toIsoDate(date: Date) {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatDisplayDate(date: string) {
  return dateFormatter.format(new Date(`${date}T12:00:00`))
}

function formatTime(dateTime: string) {
  return timeFormatter.format(new Date(dateTime))
}

function formatWeight(value: number) {
  return new Intl.NumberFormat('pl-PL', { maximumFractionDigits: 1 }).format(value)
}

function buildCalendarDays(year: number, monthIndex: number): CalendarDay[] {
  const firstDay = new Date(year, monthIndex, 1)
  const firstWeekday = (firstDay.getDay() + 6) % 7
  const start = new Date(year, monthIndex, 1 - firstWeekday)

  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(start)
    date.setDate(start.getDate() + index)

    return {
      date: toIsoDate(date),
      dayNumber: date.getDate(),
      inCurrentMonth: date.getMonth() === monthIndex,
    }
  })
}

export function CalendarPage() {
  const now = new Date()
  const [visibleDate, setVisibleDate] = useState(new Date(now.getFullYear(), now.getMonth(), 1))
  const [selectedDate, setSelectedDate] = useState(toIsoDate(now))
  const [selectedWorkoutId, setSelectedWorkoutId] = useState<number | null>(null)
  const [workouts, setWorkouts] = useState<CalendarWorkoutResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const year = visibleDate.getFullYear()
  const monthIndex = visibleDate.getMonth()
  const month = monthIndex + 1

  const days = useMemo(() => buildCalendarDays(year, monthIndex), [year, monthIndex])

  const workoutsByDate = useMemo(() => {
    return workouts.reduce<Record<string, CalendarWorkoutResponse[]>>((acc, workout) => {
      acc[workout.date] = [...(acc[workout.date] ?? []), workout]
      return acc
    }, {})
  }, [workouts])

  const selectedWorkouts = workoutsByDate[selectedDate] ?? []
  const selectedWorkout =
    selectedWorkouts.find((workout) => workout.workoutId === selectedWorkoutId) ?? selectedWorkouts[0] ?? null
  const monthWorkoutsCount = workouts.length
  const monthVolume = workouts.reduce((sum, workout) => sum + (workout.totalVolumeKg ?? 0), 0)
  const selectedWorkoutSets = selectedWorkout?.sets ?? []
  const selectedExerciseNames = selectedWorkout
    ? Array.from(new Set(selectedWorkoutSets.map((set) => set.exerciseName)))
    : []

  useEffect(() => {
    const loadCalendar = async () => {
      setError('')
      setLoading(true)
      try {
        const data = await planningApi.getCalendar(month, year)
        setWorkouts(data)
        setSelectedWorkoutId(null)
      } catch {
        setError('Nie udalo sie pobrac kalendarza.')
        setWorkouts([])
      } finally {
        setLoading(false)
      }
    }

    void loadCalendar()
  }, [month, year])

  const changeMonth = (offset: number) => {
    setVisibleDate((current) => new Date(current.getFullYear(), current.getMonth() + offset, 1))
  }

  const goToToday = () => {
    const today = new Date()
    setVisibleDate(new Date(today.getFullYear(), today.getMonth(), 1))
    setSelectedDate(toIsoDate(today))
    setSelectedWorkoutId(null)
  }

  const selectDate = (date: string) => {
    setSelectedDate(date)
    setSelectedWorkoutId(null)
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Kalendarz</h1>
          <p className="mt-1 text-sm text-gray-500">Przegladaj treningi zapisane w wybranym miesiacu.</p>
        </div>
        <div className="flex gap-2">
          <Button type="button" variant="secondary" onClick={() => changeMonth(-1)}>
            Poprzedni
          </Button>
          <Button type="button" variant="secondary" onClick={goToToday}>
            Dzisiaj
          </Button>
          <Button type="button" variant="secondary" onClick={() => changeMonth(1)}>
            Nastepny
          </Button>
        </div>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="mb-5 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <Card title="Miesiac">
          <p className="text-3xl font-bold text-primary">
            {monthNames[monthIndex]} {year}
          </p>
        </Card>
        <Card title="Treningi">
          <p className="text-3xl font-bold text-primary">{monthWorkoutsCount}</p>
        </Card>
        <Card title="Objetosc">
          <p className="text-3xl font-bold text-primary">{formatWeight(monthVolume)} kg</p>
        </Card>
        <Card title="Wybrany dzien">
          <p className="text-lg font-bold capitalize text-primary">{formatDisplayDate(selectedDate)}</p>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[1fr_420px]">
        <Card title="Widok miesiaca">
          {loading ? (
            <div className="flex h-96 items-center justify-center text-sm text-gray-500">
              Ladowanie kalendarza...
            </div>
          ) : (
            <div>
              <div className="grid grid-cols-7 border-b border-gray-200 pb-2 text-center text-xs font-semibold uppercase text-gray-500">
                {dayLabels.map((day) => (
                  <div key={day}>{day}</div>
                ))}
              </div>
              <div className="mt-2 grid grid-cols-7 gap-2">
                {days.map((day) => {
                  const dayWorkouts = workoutsByDate[day.date] ?? []
                  const isSelected = selectedDate === day.date
                  const isToday = toIsoDate(new Date()) === day.date

                  return (
                    <button
                      key={day.date}
                      type="button"
                      onClick={() => selectDate(day.date)}
                      className={`min-h-32 rounded-lg border p-2 text-left transition-colors ${
                        isSelected
                          ? 'border-primary bg-primary-light'
                          : 'border-gray-200 bg-white hover:bg-gray-50'
                      } ${day.inCurrentMonth ? 'text-gray-900' : 'text-gray-400'}`}
                    >
                      <div className="flex items-center justify-between">
                        <span
                          className={`flex h-7 w-7 items-center justify-center rounded-full text-sm font-semibold ${
                            isToday ? 'bg-primary text-white' : ''
                          }`}
                        >
                          {day.dayNumber}
                        </span>
                        {dayWorkouts.length > 0 && (
                          <span className="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-600">
                            {dayWorkouts.length}
                          </span>
                        )}
                      </div>
                      <div className="mt-2 space-y-1">
                        {dayWorkouts.slice(0, 2).map((workout) => (
                          <div
                            key={workout.workoutId}
                            className="rounded bg-primary/10 px-2 py-1 text-xs font-medium text-primary-hover"
                          >
                            <div className="truncate">{workout.workoutName}</div>
                            <div className="mt-0.5 flex items-center gap-2 text-[11px] font-normal text-gray-600">
                              <span>{workout.totalSets ?? 0} serii</span>
                              <span>{formatWeight(workout.totalVolumeKg ?? 0)} kg</span>
                            </div>
                          </div>
                        ))}
                        {dayWorkouts.length > 2 && (
                          <div className="text-xs text-gray-500">+{dayWorkouts.length - 2} wiecej</div>
                        )}
                      </div>
                    </button>
                  )
                })}
              </div>
            </div>
          )}
        </Card>

        <Card title="Szczegoly dnia">
          <div className="mb-4 rounded-lg bg-gray-50 px-3 py-2">
            <p className="text-sm font-semibold capitalize text-gray-900">{formatDisplayDate(selectedDate)}</p>
            <p className="mt-0.5 text-xs text-gray-500">
              {selectedWorkouts.length === 0
                ? 'Brak zapisanych treningow'
                : `${selectedWorkouts.length} ${selectedWorkouts.length === 1 ? 'trening' : 'treningi'}`}
            </p>
          </div>
          {selectedWorkouts.length === 0 ? (
            <p className="text-sm text-gray-500">Brak treningow w tym dniu.</p>
          ) : (
            <div>
              <div className="mb-4 space-y-2">
                {selectedWorkouts.map((workout) => {
                  const isActive = selectedWorkout?.workoutId === workout.workoutId

                  return (
                    <button
                      key={workout.workoutId}
                      type="button"
                      onClick={() => setSelectedWorkoutId(workout.workoutId)}
                      className={`w-full rounded-lg border p-3 text-left transition-colors ${
                        isActive
                          ? 'border-primary bg-primary-light'
                          : 'border-gray-200 bg-white hover:bg-gray-50'
                      }`}
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <p className="truncate text-sm font-semibold text-gray-900">{workout.workoutName}</p>
                          <p className="mt-1 text-xs text-gray-500">
                            Zakonczony o {formatTime(workout.completedAt)}
                          </p>
                        </div>
                        <span className="shrink-0 rounded-full bg-white px-2 py-0.5 text-xs font-medium text-gray-600">
                          {workout.totalSets ?? 0} serii
                        </span>
                      </div>
                    </button>
                  )
                })}
              </div>

              {selectedWorkout && (
                <div className="border-t border-gray-200 pt-4">
                  <div className="grid grid-cols-2 gap-3">
                    <div className="rounded-lg bg-gray-50 p-3">
                      <p className="text-xs font-medium uppercase text-gray-500">Objetosc</p>
                      <p className="mt-1 text-lg font-bold text-gray-900">
                        {formatWeight(selectedWorkout.totalVolumeKg ?? 0)} kg
                      </p>
                    </div>
                    <div className="rounded-lg bg-gray-50 p-3">
                      <p className="text-xs font-medium uppercase text-gray-500">Cwiczenia</p>
                      <p className="mt-1 text-lg font-bold text-gray-900">{selectedExerciseNames.length}</p>
                    </div>
                  </div>

                  <div className="mt-4">
                    <h3 className="text-sm font-semibold text-gray-900">Wykonane cwiczenia</h3>
                    <div className="mt-3 space-y-3">
                      {selectedExerciseNames.map((exerciseName) => {
                        const sets = selectedWorkoutSets.filter((set) => set.exerciseName === exerciseName)
                        const muscleGroup = sets[0]?.muscleGroup

                        return (
                          <div key={exerciseName} className="rounded-lg border border-gray-200 p-3">
                            <div className="flex items-start justify-between gap-3">
                              <div>
                                <p className="text-sm font-semibold text-gray-900">{exerciseName}</p>
                                <p className="mt-0.5 text-xs text-gray-500">{muscleGroup}</p>
                              </div>
                              <span className="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-600">
                                {sets.length} serii
                              </span>
                            </div>
                            <div className="mt-3 grid grid-cols-3 gap-2 text-xs font-medium text-gray-500">
                              <span>Seria</span>
                              <span>Powt.</span>
                              <span>Ciezar</span>
                            </div>
                            <div className="mt-1 space-y-1">
                              {sets.map((set, index) => (
                                <div
                                  key={`${set.exerciseId}-${set.performedAt}-${index}`}
                                  className="grid grid-cols-3 gap-2 rounded bg-gray-50 px-2 py-1.5 text-sm text-gray-700"
                                >
                                  <span>{index + 1}</span>
                                  <span>{set.reps}</span>
                                  <span>{formatWeight(set.weightKg)} kg</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        )
                      })}
                    </div>
                  </div>
                </div>
              )}
            </div>
          )}
        </Card>
      </div>
    </div>
  )
}
