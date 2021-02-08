
#define MyAppName "defi-portfolio"
#define MyAppVersion "1.0"
#define MyAppPublisher "DeFi Portfolio"
#define MyAppURL "https://github.com/DeFi-PortfolioManagement/defi-portfoliomanager"
#define MyAppExeName "defi-portfolio.exe"
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
OutputDir=C:\Users\User\AppData\Local\Programs\defi-portfolio
OutputBaseFilename=defi-portfolio
SetupIconFile=C:\Project\test\defi-portfoliomanager\jar\src\icons\DefiIcon.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "defi-portfolio.deps.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "defi-portfolio.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "defi-portfolio.pdb"; DestDir: "{app}"; Flags: ignoreversion
Source: "defi-portfolio.runtimeconfig.dev.json"; DestDir: "{app}"; Flags: ignoreversion                                                                            
Source: "defi-portfolio.runtimeconfig.json"; DestDir: "{app}"; Flags: ignoreversion
Source: "defi-portfolio_jar.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\src\*"; DestDir: "{app}\src"; Flags: ignoreversion recursesubdirs

[Registry]
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocExt}\OpenWithProgids"; ValueType: string; ValueName: "{#MyAppAssocKey}"; ValueData: ""; Flags: uninsdeletevalue
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}"; ValueType: string; ValueName: ""; ValueData: "{#MyAppAssocName}"; Flags: uninsdeletekey
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName},0"
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""
Root: HKA; Subkey: "Software\Classes\Applications\{#MyAppExeName}\SupportedTypes"; ValueType: string; ValueName: ".myp"; ValueData: ""

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

