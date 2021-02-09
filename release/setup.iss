
#define MyAppName "defi-portfolio"
#define MyAppVersion "1.0"
#define MyAppPublisher "DeFi Portfolio"
#define MyAppURL "https://github.com/DeFi-PortfolioManagement/defi-portfoliomanager"
#define MyAppExeName "C:\Project\test\defi-portfoliomanager\src\exe\defi-portfolio\defi-portfolio\bin\Release\netcoreapp3.1\defi-portfolio.exe"
#define MyApp "defi-portfolio.exe"
#define MyAppAssocName "defiportfolio"
#define MyAppAssocExt ".defiportfolio"
#define MyAppAssocKey StringChange(MyAppAssocName, " ", "") + MyAppAssocExt

[Setup]
AppId={{6C8D5AB7-01ED-4C01-9A36-9523602C59E8}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
DisableDirPage=yes
ChangesAssociations=yes
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputDir=\setup
OutputBaseFilename=defi-portfolio
SetupIconFile="..\out\production\defi-portfolio\icons\DefiIcon.ico"
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: checkedonce

[Files]
Source: "{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\exe\defi-portfolio\defi-portfolio\bin\Release\netcoreapp3.1\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs
Source: "..\out\artifacts\defi_portfolio_jar\defi-portfolio_jar.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\out\production\defi-portfolio\*"; DestDir: "{app}\src"; Flags: ignoreversion recursesubdirs
Source: "..\src\portfolio\libraries\javafx-sdk-11.0.2\*"; DestDir: "{app}\javafx-sdk-11.0.2"; Flags: ignoreversion recursesubdirs

[Registry]
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocExt}\OpenWithProgids"; ValueType: string; ValueName: "{#MyAppAssocKey}"; ValueData: ""; Flags: uninsdeletevalue
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}"; ValueType: string; ValueName: ""; ValueData: "{#MyAppAssocName}"; Flags: uninsdeletekey
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName},0"
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyApp}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyApp}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyApp}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

