import { useAuthStore } from '@/shared/store/auth.store'
import { Card } from '@/shared/components'

export function DashboardPage() {
  const user = useAuthStore((s) => s.user)

  return (
    <div>
      <h1 className="mb-1 text-2xl font-bold text-gray-900">
        Cześć, {user?.username} 👋
      </h1>
      <p className="mb-6 text-sm text-gray-500">Oto Twoje podsumowanie</p>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card title="Treningi w tym tygodniu">
          <p className="text-3xl font-bold text-primary">—</p>
        </Card>
        <Card title="Łączny wolumen (kg)">
          <p className="text-3xl font-bold text-primary">—</p>
        </Card>
        <Card title="Aktywna passa">
          <p className="text-3xl font-bold text-primary">— dni</p>
        </Card>
        <Card title="Aktywne wyzwania">
          <p className="text-3xl font-bold text-primary">—</p>
        </Card>
      </div>
    </div>
  )
}
