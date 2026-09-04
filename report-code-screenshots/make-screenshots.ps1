Add-Type -AssemblyName System.Drawing
$projectRoot = Split-Path -Parent $PSScriptRoot
$keywordPattern='\b(?:public|private|protected|class|interface|extends|return|new|if|else|try|catch|throw|boolean|int|void|static|final|enum|record|package|import|const|let|var|function|async|await|export|default|from|true|false|null|this)\b'
$typePattern='\b(?:String|Long|Integer|BigDecimal|LocalDate|LocalDateTime|Map|List|Set|Optional|Appointment|Patient|Dentist|Treatment|Bill|ResponseEntity|HttpStatus)\b'
function Draw-CodeLine($graphics,$font,$line,$x,$y){
 $lineNo=$line.Substring(0,[Math]::Min(6,$line.Length));$code=if($line.Length -gt 6){$line.Substring(6)}else{''}
 $lineBrush=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(133,133,133));$graphics.DrawString($lineNo.Trim(),$font,$lineBrush,$x+8,$y);$lineBrush.Dispose()
 $cursor=$x+78
 $pattern='(//.*$|"(?:\\.|[^"\\])*"|''(?:\\.|[^''\\])*''|@\w+|'+$keywordPattern+'|'+$typePattern+'|\b\d+(?:\.\d+)?\b)'
 foreach($part in [regex]::Split($code,$pattern)){if($part -eq ''){continue}
  $isString=$part.StartsWith([char]34) -or $part.StartsWith([char]39)
  $color=if($part -match '^//'){[System.Drawing.Color]::FromArgb(106,153,85)}elseif($isString){[System.Drawing.Color]::FromArgb(206,145,120)}elseif($part -match '^@'){[System.Drawing.Color]::FromArgb(220,220,170)}elseif($part -match ('^'+$keywordPattern+'$')){[System.Drawing.Color]::FromArgb(197,134,192)}elseif($part -match ('^'+$typePattern+'$')){[System.Drawing.Color]::FromArgb(78,201,176)}elseif($part -match '^\d'){[System.Drawing.Color]::FromArgb(181,206,168)}else{[System.Drawing.Color]::FromArgb(212,212,212)}
  $brush=New-Object System.Drawing.SolidBrush($color);$graphics.DrawString($part,$font,$brush,$cursor,$y,[System.Drawing.StringFormat]::GenericTypographic);$cursor+=$graphics.MeasureString($part,$font,[int]::MaxValue,[System.Drawing.StringFormat]::GenericTypographic).Width;$brush.Dispose()
 }
}
$shots = @(
 @{Name='C3-double-booking-test.png';File='backend/src/test/java/com/sunrise/dental/service/ClinicServiceTest.java';From=194;To=213;Title='Automated Double-Booking Prevention Test'},
 @{Name='C4-billing-calculation-test.png';File='backend/src/test/java/com/sunrise/dental/service/ClinicServiceTest.java';From=124;To=155;Title='Automated Billing Calculation Test'},
 @{Name='C2-service-test-class.png';File='backend/src/test/java/com/sunrise/dental/service/ClinicServiceTest.java';From=8;To=47;Title='JUnit and Mockito Service-Layer Test Configuration'},
 @{Name='01-appointment-registration.png';File='frontend/src/pages/Appointments.jsx';From=37;To=65;Title='Appointment Registration - React Frontend'},
 @{Name='02-appointment-number-and-double-booking.png';File='backend/src/main/java/com/sunrise/dental/service/ClinicService.java';From=82;To=113;Title='Appointment Number Generation and Double-Booking Prevention'},
 @{Name='03-appointment-status-and-search.png';File='backend/src/main/java/com/sunrise/dental/service/ClinicService.java';From=115;To=133;Title='Cancel, Complete and Search Appointment'},
 @{Name='04-billing-calculation.png';File='backend/src/main/java/com/sunrise/dental/service/ClinicService.java';From=142;To=163;Title='Billing Calculation and Duplicate-Bill Prevention'},
 @{Name='05-rest-appointment-endpoints.png';File='backend/src/main/java/com/sunrise/dental/controller/ClinicController.java';From=4;To=25;Title='Appointment REST Web-Service Endpoints'},
 @{Name='06-appointment-repository.png';File='backend/src/main/java/com/sunrise/dental/repository/AppointmentRepository.java';From=1;To=16;Title='Repository and Appointment-Number Search'},
 @{Name='07-jwt-authentication-filter.png';File='backend/src/main/java/com/sunrise/dental/security/JwtFilter.java';From=24;To=45;Title='JWT Authentication Filter'},
 @{Name='08-role-security-configuration.png';File='backend/src/main/java/com/sunrise/dental/config/SecurityConfig.java';From=35;To=53;Title='Protected Routes and Role-Based Security'},
 @{Name='09-validation-dto.png';File='backend/src/main/java/com/sunrise/dental/dto/Requests.java';From=20;To=51;Title='Validated Appointment Request DTO'},
 @{Name='10-friendly-error-handling.png';File='backend/src/main/java/com/sunrise/dental/exception/ApiExceptionHandler.java';From=10;To=43;Title='Centralized Friendly Error Handling'},
 @{Name='11-receipt-printing.png';File='frontend/src/pages/Billing.jsx';From=7;To=17;Title='Printable and Downloadable Receipt'},
 @{Name='12-mysql-jpa-configuration.png';File='backend/src/main/resources/application.yml';From=1;To=15;Title='MySQL and JPA Configuration';Mask=$true}
)
foreach($shot in $shots){
 $source=Get-Content -LiteralPath (Join-Path $projectRoot $shot.File)
 $lines=@(for($i=$shot.From;$i -le [Math]::Min($shot.To,$source.Count);$i++){
  $line=$source[$i-1]
  if($shot.Mask -and $line -match '^\s*password:'){$line='    password: ${DB_PASSWORD:********}'}
  $prefix='{0,4}  ' -f $i
  $maxChars=132
  if($line.Length -le $maxChars){$prefix+$line}
  else{
   $remaining=$line;$first=$true
   while($remaining.Length -gt $maxChars){
    $cut=$remaining.LastIndexOf(' ',$maxChars)
    if($cut -lt 60){$cut=$maxChars}
    $(if($first){$prefix}else{'      '})+$remaining.Substring(0,$cut)
    $remaining=$remaining.Substring($cut).TrimStart();$first=$false
   }
   $(if($first){$prefix}else{'      '})+$remaining
  }
 })
 $fontSize=if($lines.Count -gt 34){17}elseif($lines.Count -gt 28){18}else{20}
 $font=New-Object System.Drawing.Font('Consolas',$fontSize,[System.Drawing.FontStyle]::Regular,[System.Drawing.GraphicsUnit]::Pixel)
 $titleFont=New-Object System.Drawing.Font('Segoe UI Semibold',25,[System.Drawing.FontStyle]::Bold,[System.Drawing.GraphicsUnit]::Pixel)
 $pathFont=New-Object System.Drawing.Font('Segoe UI',15,[System.Drawing.FontStyle]::Regular,[System.Drawing.GraphicsUnit]::Pixel)
 $lineHeight=[int]($fontSize*1.55);$height=137+($lines.Count*$lineHeight);$width=1600
 $bitmap=New-Object System.Drawing.Bitmap($width,$height);$g=[System.Drawing.Graphics]::FromImage($bitmap)
 $g.TextRenderingHint=[System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit;$g.Clear([System.Drawing.Color]::FromArgb(30,30,30))
 $header=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(37,37,38));$accent=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(0,150,136))
 $titleBrush=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(245,245,245));$pathBrush=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(170,170,170));$codeBrush=New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(220,220,220))
 $g.FillRectangle($header,0,0,$width,92);$g.FillRectangle($accent,0,0,9,$height);$g.DrawString($shot.Title,$titleFont,$titleBrush,28,14);$g.DrawString($shot.File,$pathFont,$pathBrush,30,54)
 $y=108;foreach($line in $lines){Draw-CodeLine $g $font $line 27 $y;$y+=$lineHeight}
 $bitmap.Save((Join-Path $PSScriptRoot $shot.Name),[System.Drawing.Imaging.ImageFormat]::Png)
 $g.Dispose();$bitmap.Dispose();$font.Dispose();$titleFont.Dispose();$pathFont.Dispose();$header.Dispose();$accent.Dispose();$titleBrush.Dispose();$pathBrush.Dispose();$codeBrush.Dispose()
}
Write-Output "Created $($shots.Count) screenshots."
