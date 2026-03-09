$ErrorActionPreference = "Stop"

$baseDir = Split-Path -Parent $PSScriptRoot
$wrapperDir = Join-Path $baseDir "wrapper"
$propsPath = Join-Path $wrapperDir "maven-wrapper.properties"
$jarPath = Join-Path $wrapperDir "maven-wrapper.jar"

if (!(Test-Path $propsPath)) {
  throw "Missing $propsPath"
}

$props = Get-Content $propsPath | Where-Object { $_ -match "=" } | ForEach-Object {
  $parts = $_.Split("=", 2)
  @{ Key = $parts[0].Trim(); Value = $parts[1].Trim() }
}

$wrapperUrl = ($props | Where-Object { $_.Key -eq "wrapperUrl" }).Value
if ([string]::IsNullOrWhiteSpace($wrapperUrl)) {
  throw "wrapperUrl not found in $propsPath"
}

New-Item -ItemType Directory -Force -Path $wrapperDir | Out-Null

Write-Host "Downloading Maven Wrapper jar..."
Write-Host "  $wrapperUrl"

Invoke-WebRequest -Uri $wrapperUrl -OutFile $jarPath

Write-Host "Saved to $jarPath"

