export default function AuthCard({ children }) {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        {/* Logo */}
        <div className="text-center mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">StudentLink</h1>
          <p className="text-sm text-gray-500 mt-1">Your campus, connected</p>
        </div>

        {/* Card */}
        <div className="bg-white rounded-2xl border border-gray-200 p-8 shadow-sm">
          {children}
        </div>
      </div>
    </div>
  )
}
