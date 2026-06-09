import { FormEvent, useEffect, useMemo, useState } from 'react'
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { Button, Card, Input } from '@/shared/components'
import { planningApi } from '../api'
import type {
  ExerciseDto,
  StrengthProgressPointResponse,
  WeightProgressPointResponse,
} from '../types'

function todayIso() {
  return new Date().toISOString().slice(0, 10)
}

function formatNumber(value: number | null | undefined, suffix = '') {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '-'
  }
  return `${Number(value).toFixed(1)}${suffix}`
}

export function StatsPage() {
  const [weightData, setWeightData] = useState<WeightProgressPointResponse[]>([])
  const [strengthData, setStrengthData] = useState<StrengthProgressPointResponse[]>([])
  const [exercises, setExercises] = useState<ExerciseDto[]>([])
  const [selectedExerciseId, setSelectedExerciseId] = useState('')
  const [weightForm, setWeightForm] = useState({ date: todayIso(), weightKg: '' })
  const [loading, setLoading] = useState(true)
  const [savingWeight, setSavingWeight] = useState(false)
  const [error, setError] = useState('')

  const latestWeight = weightData[weightData.length - 1]?.weightKg
  const previousWeight = weightData[weightData.length - 2]?.weightKg
  const weightChange = useMemo(() => {
    if (latestWeight === undefined || previousWeight === undefined) {
      return null
    }
    return Number(latestWeight) - Number(previousWeight)
  }, [latestWeight, previousWeight])

  const bestStrength = useMemo(() => {
    if (strengthData.length === 0) {
      return null
    }
    return Math.max(...strengthData.map((point) => Number(point.maxWeight)))
  }, [strengthData])

  const loadBaseData = async () => {
    setError('')
    setLoading(true)
    try {
      const [weights, exerciseList] = await Promise.all([
        planningApi.getWeightProgress(),
        planningApi.getExercises(),
      ])
      setWeightData(weights)
      setExercises(exerciseList)
      if (exerciseList[0]) {
        setSelectedExerciseId(exerciseList[0].id)
      }
    } catch {
      setError('Nie udalo sie pobrac statystyk.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadBaseData()
  }, [])

  useEffect(() => {
    if (!selectedExerciseId) {
      setStrengthData([])
      return
    }

    const loadStrength = async () => {
      try {
        const data = await planningApi.getStrengthProgress(selectedExerciseId)
        setStrengthData(data)
      } catch {
        setStrengthData([])
      }
    }

    void loadStrength()
  }, [selectedExerciseId])

  const saveWeight = async (event: FormEvent) => {
    event.preventDefault()
    setError('')

    const weightKg = Number(weightForm.weightKg)
    if (!weightForm.date || !weightKg || weightKg <= 0) {
      setError('Podaj poprawna date i wage.')
      return
    }

    setSavingWeight(true)
    try {
      const saved = await planningApi.saveWeightEntry({
        userId: null,
        date: weightForm.date,
        weightKg,
      })
      setWeightData((current) => {
        const withoutSameDate = current.filter((point) => point.date !== saved.date)
        return [...withoutSameDate, saved].sort((a, b) => a.date.localeCompare(b.date))
      })
      setWeightForm({ date: todayIso(), weightKg: '' })
    } catch {
      setError('Nie udalo sie zapisac wagi.')
    } finally {
      setSavingWeight(false)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Postep</h1>
        <p className="mt-1 text-sm text-gray-500">Monitoruj wage i progres silowy dla wybranego cwiczenia.</p>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="mb-5 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <Card title="Aktualna waga">
          <p className="text-3xl font-bold text-primary">{formatNumber(latestWeight, ' kg')}</p>
        </Card>
        <Card title="Zmiana">
          <p className="text-3xl font-bold text-primary">{formatNumber(weightChange, ' kg')}</p>
        </Card>
        <Card title="Najlepszy ciezar">
          <p className="text-3xl font-bold text-primary">{formatNumber(bestStrength, ' kg')}</p>
        </Card>
      </div>

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[360px_1fr]">
        <div className="space-y-5">
          <Card title="Dodaj wage">
            <form onSubmit={saveWeight} className="space-y-4">
              <Input
                label="Data"
                type="date"
                value={weightForm.date}
                onChange={(event) => setWeightForm((current) => ({ ...current, date: event.target.value }))}
                required
              />
              <Input
                label="Waga kg"
                type="number"
                min="0.1"
                step="0.1"
                value={weightForm.weightKg}
                onChange={(event) => setWeightForm((current) => ({ ...current, weightKg: event.target.value }))}
                placeholder="75.5"
                required
              />
              <Button type="submit" loading={savingWeight} className="w-full">
                Zapisz wage
              </Button>
            </form>
          </Card>

          <Card title="Cwiczenie do progresu">
            <label className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-700">Cwiczenie</span>
              <select
                value={selectedExerciseId}
                onChange={(event) => setSelectedExerciseId(event.target.value)}
                className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                {exercises.length === 0 ? (
                  <option value="">Brak cwiczen</option>
                ) : (
                  exercises.map((exercise) => (
                    <option key={exercise.id} value={exercise.id}>
                      {exercise.name}
                    </option>
                  ))
                )}
              </select>
            </label>
          </Card>
        </div>

        <div className="space-y-5">
          <Card title="Historia wagi">
            <div className="h-72">
              {loading ? (
                <div className="flex h-full items-center justify-center text-sm text-gray-500">
                  Ladowanie wykresu...
                </div>
              ) : weightData.length === 0 ? (
                <div className="flex h-full items-center justify-center text-sm text-gray-500">
                  Brak wpisow wagi.
                </div>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={weightData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="date" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 12 }} domain={['dataMin - 2', 'dataMax + 2']} />
                    <Tooltip />
                    <Line
                      type="monotone"
                      dataKey="weightKg"
                      name="Waga kg"
                      stroke="#22C55E"
                      strokeWidth={3}
                      dot={{ r: 4 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              )}
            </div>
          </Card>

          <Card title="Progres silowy">
            <div className="h-72">
              {strengthData.length === 0 ? (
                <div className="flex h-full items-center justify-center text-sm text-gray-500">
                  Brak danych dla wybranego cwiczenia.
                </div>
              ) : (
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={strengthData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                    <XAxis dataKey="date" tick={{ fontSize: 12 }} />
                    <YAxis tick={{ fontSize: 12 }} domain={['dataMin - 5', 'dataMax + 5']} />
                    <Tooltip />
                    <Line
                      type="monotone"
                      dataKey="maxWeight"
                      name="Maks. ciezar kg"
                      stroke="#2563EB"
                      strokeWidth={3}
                      dot={{ r: 4 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              )}
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
