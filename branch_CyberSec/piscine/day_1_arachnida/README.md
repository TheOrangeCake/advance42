# Arachnida

Cybersecurity Piscine — introductory project to web scraping and metadata.

Two programs:
- **spider** (ex01) — recursively scrapes images from a website
- **scorpion** (ex02) — parses image files and displays their EXIF/metadata

## Requirements

- Java 25+
- Maven 3.x

## Build

```bash
./mvnw clean package
```

JARs are written to `spider/target/spider.jar` and `scorpion/target/scorpion.jar`.

---

## Ex01 — Spider

Fetches a web page, extracts images (`jpg`, `jpeg`, `png`, `gif`, `bmp`) and optionally follows same-host links up to a configurable depth.

### Usage

```
java -jar spider/target/spider.jar [-rlp] [params] URL
```

### Options

| Flag | Argument | Description |
|------|----------|-------------|
| `-r` | — | Enable recursive crawl (default depth: 5) |
| `-l` | `N` | Set max crawl depth to `N` (requires `-r`; if `-l` is used alone, `-r` is auto-enabled) |
| `-p` | `PATH` | Directory to save downloaded images (default: `./data/`) |

### Examples

Download images from a single page:
```bash
java -jar spider/target/spider.jar https://www.example.com
```

Crawl recursively up to depth 3, save to `./images/`:
```bash
java -jar spider/target/spider.jar -rl 3 -p ./images/ https://www.example.com
```

Crawl with default depth (5):
```bash
java -jar spider/target/spider.jar -r https://www.example.com
```

### Notes

- Only links on the **same host** as the starting URL are followed.
- HTTP connect and response timeouts are set to 10 seconds per request.
- Redirection not handled.

---

## Ex02 — Scorpion

Receives one or more image files and parses them for EXIF and other metadata, displaying the results on screen. Supports the same extensions as spider (`jpg`, `jpeg`, `png`, `gif`, `bmp`).

### Usage

```
java -jar scorpion/target/scorpion.jar FILE1 [FILE2 ...]
```

### Output

For each file, scorpion prints:
- **Basic attributes** — file name, format, size, creation/modification/access timestamps
- **Image attributes** — format-specific fields (dimensions, color type, bit depth, etc.)
- **EXIF metadata** — grouped by category (Image, Exposure, Date/Time, GPS, Lens, etc.) when present

### Format support

| Format | Basic attrs | EXIF |
|--------|-------------|------|
| jpg/jpeg | ✅ | ✅ Full TIFF/IFD parsing |
| png | ✅ + IHDR/pHYs/tIME/text chunks | ✅ via eXIf chunk |
| gif | ✅ + version, dimensions, color table | — |
| bmp | ✅ + dimensions, BPP, compression | — |

---

## Project Structure

```
arachnida/
├── spider/
│   ├── src/main/java/com/nguyen/spider/
│   │   ├── Spider.java          # Entry point, wires components
│   │   ├── Crawler.java         # BFS link traversal
│   │   ├── HtmlParser.java      # Extracts href/src attributes
│   │   ├── ImageDownloader.java # Downloads and saves images
│   │   ├── OptionConfig.java    # CLI argument parser
│   │   └── ParseResult.java     # Result record (urls + images)
│   └── pom.xml
├── scorpion/
│   ├── src/main/java/com/nguyen/scorpion/
│   │   ├── Scorpion.java        # Entry point
│   │   ├── chain/               # Chain of responsibility: Validator → Parser → Printer
│   │   ├── model/               # ImageContext, ExifTag enum
│   │   └── parser/              # JpgMetaParser, PngMetaParser, TiffParser, EndianReader
│   └── pom.xml
└── subject/
    └── en.subject.pdf
```
