param(
    [switch]$NoBuild,
    [int]$TimeoutSeconds = 120
)

$ErrorActionPreference = "Stop"

function Wait-HttpReady {
    param(
        [string]$Name,
        [string]$Url,
        [int]$Timeout
    )

    $deadline = (Get-Date).AddSeconds($Timeout)

    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $Url -Method Get -UseBasicParsing -TimeoutSec 5
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 400) {
                return $true
            }
        }
        catch {
            Start-Sleep -Milliseconds 1500
        }

        Start-Sleep -Milliseconds 500
    }

    Write-Host "[WARN] Timed out waiting for $Name ($Url)" -ForegroundColor Yellow
    return $false
}

$composeArgs = @("compose", "up")
if (-not $NoBuild) {
    $composeArgs += "--build"
}
$composeArgs += "-d"

Write-Host "Starting containers..." -ForegroundColor Cyan
docker @composeArgs

if ($LASTEXITCODE -ne 0) {
    throw "docker compose up failed."
}

$checks = @(
    @{ Name = "API Health"; Url = "http://localhost:8080/actuator/health" },
    @{ Name = "Swagger UI"; Url = "http://localhost:8080/swagger-ui.html" },
    @{ Name = "Prometheus"; Url = "http://localhost:9090/-/ready" },
    @{ Name = "Grafana"; Url = "http://localhost:3000/login" },
    @{ Name = "Loki"; Url = "http://localhost:3100/ready" }
)

Write-Host "Waiting for services to report ready..." -ForegroundColor Cyan
foreach ($check in $checks) {
    [void](Wait-HttpReady -Name $check.Name -Url $check.Url -Timeout $TimeoutSeconds)
}

$banner = @"
 .d8888b.  8888888b.  8888888b.              d8888 8888888b. 8888888 
d88P  Y88b 888   Y88b 888   Y88b            d88888 888   Y88b  888   
Y88b.      888    888 888    888           d88P888 888    888  888   
 "Y888b.   888   d88P 888   d88P          d88P 888 888   d88P  888   
    "Y88b. 8888888P"  8888888P"          d88P  888 8888888P"   888   
      "888 888        888               d88P   888 888         888   
Y88b  d88P 888        888              d8888888888 888         888   
 "Y8888P"  888        888             d88P     888 888       8888888                                              
                                                                     
API + Observability stack is ready.
"@

Write-Host $banner -ForegroundColor Green

Write-Host "Links:" -ForegroundColor Cyan
Write-Host "  API          http://localhost:8080"
Write-Host "  Swagger UI   http://localhost:8080/swagger-ui.html"
Write-Host "  Health       http://localhost:8080/actuator/health"
Write-Host "  Prometheus   http://localhost:9090"
Write-Host "  Grafana      http://localhost:3000  (admin/admin)"
Write-Host "  Loki         http://localhost:3100"
