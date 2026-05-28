import { useAuthStore } from '@/shared/store/auth.store'
import { Card } from '@/shared/components'

export function ProfilePage() {
  const user = useAuthStore((s) => s.user)

  return (
    <div>
      <h1 className="mb-6 text-2xl font-bold text-gray-900">Profil</h1>
      <Card title="Dane konta" className="max-w-md">
        <dl className="flex flex-col gap-3 text-sm">
          <div className="flex justify-between">
            <dt className="text-gray-500">Nazwa użytkownika</dt>
            <dd className="font-medium">{user?.username}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-gray-500">Email</dt>
            <dd className="font-medium">{user?.email}</dd>
          </div>
        </dl>
      </Card>
    </div>
  )
}
