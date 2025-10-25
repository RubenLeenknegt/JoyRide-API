# powershell
param(
  [switch]$SkipTests
)

# Stop on first error
$ErrorActionPreference = 'Stop'

$gradlew = Join-Path $PSScriptRoot 'gradlew.bat'
if (Test-Path $gradlew) {
    Write-Host "Building with gradlew..."
    $gradleArgs = @('clean','build')
    if ($SkipTests) { $gradleArgs += '-x'; $gradleArgs += 'test' }
    & $gradlew @gradleArgs
} else {
    Write-Host "Building with system gradle..."
    if ($SkipTests) { gradle clean build -x test } else { gradle clean build }
}
.\gradlew clean :backend:build
Write-Host "==> Bringing down compose stack (remove volumes)..."
docker compose -f docker-compose.local.yml down -v

Write-Host "==> Bringing up compose stack (rebuild images)..."
docker compose -f docker-compose.local.yml up --build -d

Write-Host "==> Done."
