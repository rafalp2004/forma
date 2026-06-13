import { useEffect, useMemo, useState } from 'react'
import { apiClient } from '@/shared/api/client'
import { useAuthStore } from '@/shared/store/auth.store'
import { Button, Card, Input } from '@/shared/components'
import type {
  ExerciseDto,
  UserDetailsResponse,
  WorkoutSessionDto,
  WorkoutSummaryDto,
} from '@/shared/types'

interface WorkoutSetForm {
  exerciseId: string
  reps: string
  weight: string
}

const today = new Date().toISOString().slice(0, 10)
const monthAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10)

const toDateTimeLocalValue = (date: Date) => {
  const offsetMs = date.getTimezoneOffset() * 60 * 1000
  return new Date(date.getTime() - offsetMs).toISOString().slice(0, 16)
}

const formatDateTime = (value: string) => {
  if (!value) return '-'
  return new Intl.DateTimeFormat('pl-PL', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

const formatNumber = (value: number) =>
  new Intl.NumberFormat('pl-PL', { maximumFractionDigits: 1 }).format(value)

const calculateMembershipDays = (createdAt?: string) => {
  if (!createdAt) return null

  const startDate = new Date(createdAt)
  if (Number.isNaN(startDate.getTime())) return null

  const dayMs = 24 * 60 * 60 * 1000
  return Math.max(1, Math.floor((Date.now() - startDate.getTime()) / dayMs) + 1)
}

const createDefaultSet = (): WorkoutSetForm => ({
  exerciseId: '',
  reps: '8',
  weight: '',
})

const isValidWeightInput = (value: string) => {
  const normalized = value.trim().toLowerCase().replace(',', '.')
  if (normalized === 'bw') return true
  if (!normalized) return false

  const parsed = Number(normalized)
  return Number.isFinite(parsed) && parsed >= 0
}

export function WorkoutsPage() {
  const user = useAuthStore((state) => state.user)
  const setUser = useAuthStore((state) => state.setUser)

  const [history, setHistory] = useState<WorkoutSummaryDto[]>([])
  const [exercises, setExercises] = useState<ExerciseDto[]>([])
  const [historyLoading, setHistoryLoading] = useState(false)
  const [userLoading, setUserLoading] = useState(true)
  const [currentUserId, setCurrentUserId] = useState<number | null>(null)
  const [memberSince, setMemberSince] = useState<string | null>(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [filters, setFilters] = useState({
    from: monthAgo,
    to: today,
  })

  const [sessionForm, setSessionForm] = useState({
    startTime: toDateTimeLocalValue(new Date(Date.now() - 60 * 60 * 1000)),
    endTime: toDateTimeLocalValue(new Date()),
    sets: [createDefaultSet()],
  })

  const exerciseById = useMemo(() => {
    return new Map(exercises.map((exercise) => [exercise.id, exercise]))
  }, [exercises])

  const summary = useMemo(() => {
    return history.reduce(
      (acc, workout) => ({
        workouts: acc.workouts + 1,
        sets: acc.sets + workout.totalSets,
        volume: acc.volume + workout.totalVolumeKg,
      }),
      { workouts: 0, sets: 0, volume: 0 }
    )
  }, [history])

  const membershipDays = useMemo(() => calculateMembershipDays(memberSince ?? undefined), [memberSince])

  const loadMembershipProfile = async () => {
    try {
      const { data } = await apiClient.get<UserDetailsResponse>('/users/me')
      setMemberSince(data.createdAt)
    } catch {
      setMemberSince(null)
    }
  }

  const resolveCurrentUserId = async () => {
    setUserLoading(true)

    try {
      if (user?.id && user.id > 0) {
        setCurrentUserId(user.id)
        return user.id
      }

      if (!user?.username) {
        setError('Nie udalo sie ustalic zalogowanego uzytkownika.')
        return null
      }

      const { data } = await apiClient.get<{ id: number; username: string; email: string }[]>(
        '/users/search',
        { params: { query: user.username } }
      )
      const currentUser = data.find((candidate) => candidate.username === user.username)

      if (!currentUser) {
        setError('Nie znaleziono zalogowanego uzytkownika w bazie.')
        return null
      }

      setUser(currentUser)
      setCurrentUserId(currentUser.id)
      return currentUser.id
    } catch {
      setError('Nie udalo sie ustalic zalogowanego uzytkownika.')
      return null
    } finally {
      setUserLoading(false)
    }
  }

  const loadHistory = async (nextFilters = filters, userId = currentUserId) => {
    if (!userId || userId < 1) {
      setError('Nie udalo sie ustalic zalogowanego uzytkownika.')
      return
    }

    setError('')
    setHistoryLoading(true)

    try {
      const { data } = await apiClient.get<WorkoutSummaryDto[]>('/workouts/history', {
        params: {
          userId,
          from: nextFilters.from,
          to: nextFilters.to,
        },
      })
      setHistory(data)
    } catch {
      setError('Nie udalo sie pobrac historii treningow.')
    } finally {
      setHistoryLoading(false)
    }
  }

  const loadExercises = async () => {
    try {
      const { data } = await apiClient.get<ExerciseDto[]>('/exercises')
      setExercises(data)
      setSessionForm((current) => {
        if (current.sets[0]?.exerciseId || data.length === 0) return current
        return {
          ...current,
          sets: current.sets.map((set, index) =>
            index === 0 ? { ...set, exerciseId: data[0].id } : set
          ),
        }
      })
    } catch {
      setError('Nie udalo sie pobrac listy cwiczen do formularza.')
    }
  }

  const updateSet = (index: number, patch: Partial<WorkoutSetForm>) => {
    setSessionForm((current) => ({
      ...current,
      sets: current.sets.map((set, setIndex) => (setIndex === index ? { ...set, ...patch } : set)),
    }))
  }

  const removeSet = (index: number) => {
    setSessionForm((current) => ({
      ...current,
      sets: current.sets.filter((_, setIndex) => setIndex !== index),
    }))
  }

  const addSet = () => {
    setSessionForm((current) => ({
      ...current,
      sets: [
        ...current.sets,
        {
          ...createDefaultSet(),
          exerciseId: exercises[0]?.id ?? '',
        },
      ],
    }))
  }

  const saveWorkout = async (event: React.FormEvent) => {
    event.preventDefault()
    setError('')
    setSuccess('')

    if (!currentUserId || currentUserId < 1) {
      setError('Nie udalo sie ustalic zalogowanego uzytkownika dla treningu.')
      return
    }

    if (new Date(sessionForm.startTime) > new Date(sessionForm.endTime)) {
      setError('Start treningu nie moze byc pozniejszy niz koniec.')
      return
    }

    const invalidSet = sessionForm.sets.some(
      (set) => !set.exerciseId || Number(set.reps) < 1 || !isValidWeightInput(set.weight)
    )

    if (invalidSet) {
      setError('Kazda seria musi miec cwiczenie, minimum 1 powtorzenie oraz ciezar albo bw.')
      return
    }

    const payload: WorkoutSessionDto = {
      userId: currentUserId,
      startTime: sessionForm.startTime,
      endTime: sessionForm.endTime,
      sets: sessionForm.sets.map((set) => ({
        exerciseId: set.exerciseId,
        reps: Number(set.reps),
        weight: set.weight.trim().toLowerCase().replace(',', '.'),
        performedAt: sessionForm.endTime,
      })),
    }

    setSaving(true)

    try {
      const { data } = await apiClient.post<string>('/workouts', payload)
      setSuccess(data)
      await loadHistory(filters, currentUserId)
    } catch (err: any) {
      const message = typeof err?.response?.data === 'string' ? err.response.data : ''
      setError(message || 'Nie udalo sie zapisac treningu.')
    } finally {
      setSaving(false)
    }
  }

  useEffect(() => {
    void loadExercises()
    void loadMembershipProfile()
  }, [])

  useEffect(() => {
    const initializeUserHistory = async () => {
      const resolvedUserId = await resolveCurrentUserId()
      if (resolvedUserId) {
        await loadHistory(filters, resolvedUserId)
      }
    }

    void initializeUserHistory()
  }, [user?.id, user?.username])

  return (
    <div>
      <div className="mb-6 overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div className="grid gap-6 p-6 lg:grid-cols-[minmax(0,1fr)_340px] lg:p-8">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.2em] text-primary">
              Twoja przestrzen treningowa
            </p>
            <h1 className="mt-3 text-4xl font-black tracking-tight text-gray-950 sm:text-5xl">
              Czas zadbać o formę.
            </h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-gray-500">
              Zapisuj nowe sesje, wracaj do historii i obserwuj, jak rosnie Twoja regularnosc.
            </p>

            <div className="mt-6 flex flex-wrap gap-3 text-sm">
              <span className="rounded-full bg-primary-light px-4 py-2 font-semibold text-primary">
                {membershipDays ? `${membershipDays} ${membershipDays === 1 ? 'dzien' : 'dni'} z nami` : 'Aktywny profil'}
              </span>
              <span className="rounded-full bg-gray-100 px-4 py-2 font-semibold text-gray-700">
                @{user?.username ?? 'uzytkownik'}
              </span>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3 rounded-xl bg-gray-50 p-4">
            <div>
              <p className="text-3xl font-black text-primary">{summary.workouts}</p>
              <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-gray-500">
                Treningi
              </p>
            </div>
            <div>
              <p className="text-3xl font-black text-primary">{summary.sets}</p>
              <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-gray-500">
                Serie
              </p>
            </div>
            <div>
              <p className="text-3xl font-black text-primary">{formatNumber(summary.volume)}</p>
              <p className="mt-1 text-xs font-semibold uppercase tracking-wide text-gray-500">
                Kg
              </p>
            </div>
          </div>
        </div>
      </div>

      {(error || success) && (
        <div
          className={`mb-4 rounded-lg border p-3 text-sm ${
            error
              ? 'border-red-100 bg-red-50 text-red-600'
              : 'border-green-100 bg-green-50 text-green-700'
          }`}
        >
          {error || success}
        </div>
      )}

      <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Card title="Treningi">
          <p className="text-3xl font-bold text-primary">{summary.workouts}</p>
          <p className="mt-1 text-xs text-gray-500">w wybranym zakresie</p>
        </Card>
        <Card title="Serie">
          <p className="text-3xl font-bold text-primary">{summary.sets}</p>
          <p className="mt-1 text-xs text-gray-500">lacznie wykonane</p>
        </Card>
        <Card title="Wolumen">
          <p className="text-3xl font-bold text-primary">{formatNumber(summary.volume)} kg</p>
          <p className="mt-1 text-xs text-gray-500">ciezar x powtorzenia</p>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1fr)_420px]">
        <div className="space-y-4">
          <Card title="Filtry historii">
            <form
              className="grid grid-cols-1 gap-3 md:grid-cols-[1fr_1fr_auto]"
              onSubmit={(event) => {
                event.preventDefault()
                void loadHistory()
              }}
            >
              <Input
                label="Od"
                type="date"
                value={filters.from}
                onChange={(event) =>
                  setFilters((current) => ({ ...current, from: event.target.value }))
                }
              />
              <Input
                label="Do"
                type="date"
                value={filters.to}
                onChange={(event) =>
                  setFilters((current) => ({ ...current, to: event.target.value }))
                }
              />
              <div className="flex items-end">
                <Button
                  type="submit"
                  loading={historyLoading || userLoading}
                  disabled={!currentUserId}
                  className="w-full md:w-auto"
                >
                  Odswiez
                </Button>
              </div>
            </form>
          </Card>

          <Card title="Historia treningow">
            {historyLoading ? (
              <p className="text-sm text-gray-500">Ladowanie historii...</p>
            ) : history.length === 0 ? (
              <div className="rounded-lg border border-dashed border-gray-200 p-8 text-center">
                <p className="font-medium text-gray-900">Brak treningow</p>
                <p className="mt-1 text-sm text-gray-500">
                  Zmien zakres dat albo zapisz nowa sesje.
                </p>
              </div>
            ) : (
              <div className="space-y-3">
                {history.map((workout) => (
                  <div key={workout.id} className="rounded-lg border border-gray-200 p-4">
                    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                      <div>
                        <p className="font-semibold text-gray-900">
                          Trening #{workout.id}
                        </p>
                        <p className="mt-1 text-sm text-gray-500">
                          {formatDateTime(workout.completedAt)}
                        </p>
                      </div>
                      <div className="flex gap-2 text-sm">
                        <span className="rounded-full bg-primary-light px-3 py-1 font-medium text-primary">
                          {workout.totalSets} serii
                        </span>
                        <span className="rounded-full bg-gray-100 px-3 py-1 font-medium text-gray-700">
                          {formatNumber(workout.totalVolumeKg)} kg
                        </span>
                      </div>
                    </div>

                    {workout.sets.length > 0 && (
                      <div className="mt-4 overflow-hidden rounded-lg border border-gray-100">
                        <table className="min-w-full divide-y divide-gray-100 text-sm">
                          <thead className="bg-gray-50 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                            <tr>
                              <th className="px-3 py-2">Cwiczenie</th>
                              <th className="px-3 py-2">Partia</th>
                              <th className="px-3 py-2 text-right">Powt.</th>
                              <th className="px-3 py-2 text-right">Ciezar</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-gray-100">
                            {workout.sets.map((set, index) => (
                              <tr key={`${workout.id}-${set.exerciseId}-${index}`}>
                                <td className="px-3 py-2 text-gray-900">{set.exerciseName}</td>
                                <td className="px-3 py-2 text-gray-600">{set.muscleGroup}</td>
                                <td className="px-3 py-2 text-right text-gray-700">{set.reps}</td>
                                <td className="px-3 py-2 text-right text-gray-700">
                                  {formatNumber(set.weightKg)} kg
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>

        <Card title="Nowy trening">
          <form className="space-y-4" onSubmit={saveWorkout}>
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <Input
                label="Start"
                type="datetime-local"
                value={sessionForm.startTime}
                onChange={(event) =>
                  setSessionForm((current) => ({ ...current, startTime: event.target.value }))
                }
              />
              <Input
                label="Koniec"
                type="datetime-local"
                value={sessionForm.endTime}
                onChange={(event) =>
                  setSessionForm((current) => ({ ...current, endTime: event.target.value }))
                }
              />
            </div>

            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium text-gray-900">Serie</p>
                <Button type="button" variant="secondary" onClick={addSet} className="px-3 py-1.5">
                  Dodaj serie
                </Button>
              </div>

              {sessionForm.sets.map((set, index) => {
                const selectedExercise = exerciseById.get(set.exerciseId)

                return (
                  <div key={index} className="rounded-lg border border-gray-200 p-3">
                    <div className="mb-3 flex items-center justify-between">
                      <p className="text-sm font-medium text-gray-900">Seria {index + 1}</p>
                      {sessionForm.sets.length > 1 && (
                        <Button
                          type="button"
                          variant="ghost"
                          onClick={() => removeSet(index)}
                          className="px-2 py-1 text-red-600 hover:bg-red-50"
                        >
                          Usun
                        </Button>
                      )}
                    </div>

                    <label className="mb-3 flex flex-col gap-1">
                      <span className="text-sm font-medium text-gray-700">Cwiczenie</span>
                      <select
                        value={set.exerciseId}
                        onChange={(event) => updateSet(index, { exerciseId: event.target.value })}
                        className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none transition-colors focus:border-primary focus:ring-2 focus:ring-primary/20"
                        required
                      >
                        <option value="" disabled>
                          Wybierz cwiczenie
                        </option>
                        {exercises.map((exercise) => (
                          <option key={exercise.id} value={exercise.id}>
                            {exercise.name}
                          </option>
                        ))}
                      </select>
                      {selectedExercise && (
                        <span className="text-xs text-gray-500">
                          {selectedExercise.muscleGroup} / {selectedExercise.equipment || 'bez sprzetu'}
                        </span>
                      )}
                    </label>

                    <div className="grid grid-cols-2 gap-3">
                      <Input
                        label="Powtorzenia"
                        type="number"
                        min="1"
                        value={set.reps}
                        onChange={(event) => updateSet(index, { reps: event.target.value })}
                      />
                      <Input
                        label="Ciezar kg lub bw"
                        type="text"
                        placeholder="np. 60 albo bw"
                        value={set.weight}
                        onChange={(event) => updateSet(index, { weight: event.target.value })}
                      />
                    </div>
                  </div>
                )
              })}
            </div>

            <Button type="submit" loading={saving} disabled={!currentUserId} className="w-full">
              Zapisz trening
            </Button>
          </form>
        </Card>
      </div>
    </div>
  )
}
