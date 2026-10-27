#!/usr/bin/env python3
"""Import one pinned MIT-licensed wallet snapshot and apply non-cryptographic Veldigo branding.

Do not change private-key handling, transaction signing or chain protocols here.
The script is intentionally deterministic and refuses accidental re-imports.
"""
from __future__ import annotations
from pathlib import Path
import argparse
import shutil
import re
import struct
import zlib
import binascii

UPSTREAM_SHA = "d6661c8d0dc02d259767f58b7cd73cabb877da18"
APP_ID = "com.brendigo.veldigo"
REPO_URL = "https://github.com/bren-wp/Veldigo-App"

def put(dst: Path, data: str) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    dst.write_text(data, encoding="utf-8")

def edit(dst: Path, old: str, new: str, must=True) -> None:
    content = dst.read_text(encoding="utf-8")
    if old not in content:
        if must:
            raise RuntimeError(f"Expected marker missing in {dst}: {old[:85]}")
        return
    put(dst, content.replace(old, new))

def v_vector(size: int) -> str:
    # Android vector coordinates are shared with the Veldigo identity system.
    s = size / 108
    def pt(v): return round(v * s, 3)
    path = f"M{pt(26)},{pt(32)} L{pt(43)},{pt(38)} L{pt(54)},{pt(70)} L{pt(65)},{pt(38)} L{pt(82)},{pt(32)} L{pt(61)},{pt(85)} L{pt(47)},{pt(85)} Z"
    return f'''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{size}dp" android:height="{size}dp"
    android:viewportWidth="{size}" android:viewportHeight="{size}">
    <path android:fillColor="#41E6C7" android:pathData="{path}"/>
</vector>
'''

def png_icon(target: Path) -> None:
    # Generate an original self-contained 512px PNG; no upstream artwork reused.
    n = 512
    outer = [(0.22,0.30),(0.40,0.36),(0.50,0.65),(0.60,0.36),
             (0.78,0.30),(0.57,0.80),(0.43,0.80)]
    def inside(x, y, points):
        hit = False
        k = len(points)-1
        for i in range(len(points)):
            a,b = points[i]; c,d = points[k]
            if (b>y) != (d>y) and x < (c-a)*(y-b)/(d-b) + a:
                hit = not hit
            k = i
        return hit
    rows = []
    for j in range(n):
        row = bytearray([0])
        for i in range(n):
            nx = (i+0.5)/n; ny = (j+0.5)/n
            if inside(nx,ny,outer):
                row += bytes((65,230,199,255))
            else:
                row += bytes((7,20,33,255))
        rows.append(bytes(row))
    def chunk(typ, dat):
        return struct.pack("!I",len(dat))+typ+dat+struct.pack("!I",binascii.crc32(typ+dat)&0xffffffff)
    target.parent.mkdir(parents=True,exist_ok=True)
    target.write_bytes(b"\x89PNG\r\n\x1a\n"+
        chunk(b'IHDR',struct.pack("!2I5B",n,n,8,6,0,0,0))+
        chunk(b'IDAT',zlib.compress(b''.join(rows),9))+
        chunk(b'IEND',b''))

def main(source: Path, dest: Path):
    if not (source / "app" / "build.gradle.kts").exists():
        raise SystemExit("Upstream Android source missing; refusing partial import")
    if (dest / "app" / "build.gradle.kts").exists():
        raise SystemExit("Android source already present; refusing to overwrite existing work.")
    excludes = {'.git','.github','.claude','LICENSE','README.md','AGENTS.md'}
    for item in source.iterdir():
        if item.name in excludes:
            continue
        output = dest / item.name
        if item.is_dir():
            shutil.copytree(item, output, dirs_exist_ok=True,
                            ignore=shutil.ignore_patterns('.git','.DS_Store'))
        else:
            shutil.copy2(item,output)

    upstream_license=source.joinpath("LICENSE").read_text(encoding="utf-8")
    put(dest / "licenses" / "Unstoppable-Wallet-MIT.txt", upstream_license)

    gradle = dest / "app" / "build.gradle.kts"
    edit(gradle, 'applicationId = "io.horizontalsystems.bankwallet"',f'applicationId = "{APP_ID}"')
    edit(gradle, 'versionCode = 184','versionCode = 1')
    edit(gradle, 'versionName = "0.53.0"','versionName = "0.1.0"')
    edit(gradle, 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_NAME", "Unstoppable")',
                 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_NAME", "Veldigo")')
    edit(gradle, 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_URL", "unstoppable.money")',
                 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_URL", "github.com/bren-wp/Veldigo-App")')
    edit(gradle, 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_ICON", "https://raw.githubusercontent.com/horizontalsystems/HS-Design/master/PressKit/UW-AppIcon-on-light.png")',
                 'buildConfigFieldString("WALLET_CONNECT_APP_META_DATA_ICON", "https://raw.githubusercontent.com/bren-wp/Veldigo-App/main/branding/veldigo-icon.png")')
    edit(gradle, 'https://github.com/horizontalsystems/unstoppable-wallet-android',REPO_URL)
    edit(gradle, 'buildConfigFieldString("COMPANY_WEB_PAGE_LINK", "https://horizontalsystems.io")',
                 f'buildConfigFieldString("COMPANY_WEB_PAGE_LINK", "{REPO_URL}")')
    edit(gradle, 'buildConfigFieldString("APP_WEB_PAGE_LINK", "https://unstoppable.money")',
                 f'buildConfigFieldString("APP_WEB_PAGE_LINK", "{REPO_URL}")')
    edit(gradle, 'buildConfigFieldString("REPORT_EMAIL", "support.unstoppable@protonmail.com")',
                 'buildConfigFieldString("REPORT_EMAIL", "")')
    # Avoid claiming verified ownership of another project's app links.
    edit(gradle, '"dev.unstoppable.money"', '"veldigo.invalid"', False)
    edit(gradle, '"unstoppable.money"', '"veldigo.invalid"', False)

    edit(dest / "settings.gradle.kts", 'rootProject.name = "Unstoppable"', 'rootProject.name = "Veldigo"')
    string_root = dest / "walletkit" / "src" / "main" / "res"
    # Keep provider names (e.g. Unstoppable Domains) unchanged.
    strings = string_root / "values" / "strings.xml"
    edit(strings,'<string name="App_Name" translatable="false">Unstoppable</string>',
                 '<string name="App_Name" translatable="false">Veldigo</string>')
    edit(strings,'<string name="DeeplinkScheme" translatable="false">unstoppable</string>',
                 '<string name="DeeplinkScheme" translatable="false">veldigo</string>')
    s = strings.read_text(encoding="utf-8")
    s = s.replace("Unstoppable Wallet suggests", "Veldigo suggests")
    s = s.replace("Enjoying Unstoppable?", "Enjoying Veldigo?")
    put(strings,s)
    for p in string_root.glob("values-*/strings.xml"):
        s=p.read_text(encoding="utf-8")
        s=s.replace('>Unstoppable</string>', '>Veldigo</string>')
        put(p,s)

    colors = string_root / "values" / "colors.xml"
    content=colors.read_text(encoding="utf-8")
    replacements = {
      "yellow_d":"#41E6C7", "yellow_l":"#27CDB0",
      "green_d":"#41E6C7", "green_l":"#27CDB0",
      "dark":"#071421","carbon":"#112337",
      "steel_dark":"#0A1B2B","steel_light":"#DCE9F4",
      "smoke":"#536C82","yellow_50":"#8041E6C7",
      "yellow_20":"#3341E6C7"
    }
    for name,value in replacements.items():
        content=re.sub(r'(<color name="'+re.escape(name)+r'">)[^<]+(</color>)',
                       lambda m: m.group(1)+value+m.group(2),content)
    put(colors,content)
    kt=string_root.parent / "java" / "io" / "horizontalsystems" / "walletkit" / "ui" / "compose" / "Color.kt"
    ktText=kt.read_text(encoding="utf-8")
    for old,new in {
      "0xFF151515":"0xFF071421","0xFF232323":"0xFF112337",
      "0xFF4B4B4B":"0xFF536C82","0xFFB3B3B3":"0xFFB5C6D7",
      "0xFF0AC18E":"0xFF41E6C7","0xFF0AA177":"0xFF27CDB0",
      "0xFFFFB700":"0xFF41E6C7","0xFFFF9D00":"0xFF27CDB0",
      "0x80FFB700":"0x8041E6C7","0x33FFB700":"0x3341E6C7"
    }.items(): ktText=ktText.replace(old,new)
    put(kt,ktText)
    theme=kt.parent/"Theme.kt"
    edit(theme, "jacob = YellowD,","jacob = YellowD,") if False else None
    edit(theme, "tyler = Black,","tyler = Dark,")
    edit(theme, "darkTheme: Boolean = isSystemInDarkTheme()","darkTheme: Boolean = true")
    bg=string_root/"values"/"launcher_background.xml"
    if bg.exists():
        put(bg, bg.read_text(encoding="utf-8").replace("#FFA800","#071421").replace("#ffa800","#071421"))
    # Replace prominent icon surfaces while preserving existing resource identifiers.
    icon_dir=string_root/"drawable"
    for name in ("launcher_main_foreground.xml","launcher_main_monochrome.xml",
                 "launcher_main_preview.xml","ic_splash_logo.xml","ic_company_logo.xml"):
        if (icon_dir/name).exists():
            size=288 if name=="ic_splash_logo.xml" else 32 if name=="ic_company_logo.xml" else 108
            put(icon_dir/name,v_vector(size))
    for file in icon_dir.glob("launcher_*_foreground.xml"):
        put(file,v_vector(108))
    for file in icon_dir.glob("launcher_*_preview.xml"):
        put(file,v_vector(108))
    put(dest/"components/icons/src/main/res/drawable/ic_app_logo_72.xml",v_vector(72))
    put(dest/"components/icons/src/main/res/drawable/ic_unstoppable_icon_20.xml",v_vector(20))
    # Debug launcher overlay: app's debug variant otherwise retains upstream branding.
    for p in (dest/"app/src").glob("*/res/drawable/ic_splash_logo.xml"):
        put(p,v_vector(288))
    png_icon(dest/"branding/veldigo-icon.png")
    print(f"Imported pinned wallet source {UPSTREAM_SHA}; branded as {APP_ID}")
    print("IMPORTANT: Upstream swap/buy/network integrations require an independent audit.")

if __name__=="__main__":
    parser=argparse.ArgumentParser()
    parser.add_argument("source",type=Path)
    parser.add_argument("destination",type=Path)
    args=parser.parse_args()
    main(args.source.resolve(),args.destination.resolve())
