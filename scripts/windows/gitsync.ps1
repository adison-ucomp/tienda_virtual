Clear-Host

Set-Location "$PSScriptRoot\..\.."

Write-Host "========================================"
Write-Host "INICIANDO FLUJO GIT"
Write-Host "========================================"
Write-Host ""

if (!(Test-Path ".git")) {
    Write-Host "ERROR: No se encontro repositorio Git" -ForegroundColor Red
    pause
    exit
}

$uuid = [guid]::NewGuid().ToString()

Write-Host "UUID generado: $uuid" -ForegroundColor Yellow
Write-Host ""

Write-Host "[1/5] Ejecutando git pull..." -ForegroundColor Cyan
git pull

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR en git pull" -ForegroundColor Red
    pause
    exit
}

Write-Host ""
Write-Host "[2/5] Ejecutando git status..." -ForegroundColor Cyan
git status

Write-Host ""
Write-Host "[3/5] Ejecutando git add -A..." -ForegroundColor Cyan
git add -A

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR en git add" -ForegroundColor Red
    pause
    exit
}

Write-Host ""
Write-Host "[4/5] Ejecutando git commit..." -ForegroundColor Cyan
git commit -m "feat: $uuid"

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR en git commit" -ForegroundColor Red
    pause
    exit
}

Write-Host ""
Write-Host "[5/5] Ejecutando git push..." -ForegroundColor Cyan
git push

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR en git push" -ForegroundColor Red
    pause
    exit
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "PROCESO FINALIZADO CORRECTAMENTE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

pause
