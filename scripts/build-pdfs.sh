#!/usr/bin/env bash
#
# build-pdfs.sh — render every Markdown file in this repository to PDF.
#
# Produces, under pdf/:
#   one PDF per Markdown file, named so they are distinguishable (22 of the
#   source files are called README.md), plus a single combined book.
#
# Requires pandoc and Google Chrome. Chrome is used as the PDF engine because
# pandoc's default needs a LaTeX installation, and Chrome renders the tables and
# code blocks in these lessons correctly without one.
#
# Usage:
#   ./scripts/build-pdfs.sh            # both languages
#   ./scripts/build-pdfs.sh --en       # English only    -> pdf/
#   ./scripts/build-pdfs.sh --tr       # Turkish only    -> pdf-tr/
#   ./scripts/build-pdfs.sh --book     # only the combined book(s)
#
set -uo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CSS="$REPO_ROOT/scripts/pdf.css"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"

# --- preflight ---------------------------------------------------------------
if ! command -v pandoc >/dev/null 2>&1; then
  echo "pandoc is not installed."
  echo "  macOS:  brew install pandoc"
  exit 1
fi

if [ ! -x "$CHROME" ]; then
  for candidate in \
      "/Applications/Chromium.app/Contents/MacOS/Chromium" \
      "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge" \
      "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"; do
    [ -x "$candidate" ] && CHROME="$candidate" && break
  done
fi

if [ ! -x "$CHROME" ]; then
  echo "No Chromium-based browser found to render PDFs."
  echo "  Install Google Chrome, or use: pandoc --pdf-engine=xelatex (needs LaTeX)"
  exit 1
fi

# --- html -> pdf -------------------------------------------------------------
render() {
  local html="$1" pdf="$2"

  rm -f "$pdf"

  # Chrome headless writes the PDF and then does not exit. Waiting on the
  # process therefore hangs forever, which is what the first version of this
  # script did. So: launch it, poll for the finished file, and kill it.
  #
  # --no-pdf-header-footer drops Chrome's URL and date furniture, and the
  # profile directory is temporary so this never touches a real browser session.
  "$CHROME" --headless --disable-gpu --no-first-run \
      --user-data-dir="$WORK/chrome-profile" \
      --no-pdf-header-footer \
      --print-to-pdf="$pdf" "file://$html" >/dev/null 2>&1 &
  local pid=$!

  # Poll for up to 30 seconds. A file is only finished when its size has
  # stopped growing, so require two consecutive equal readings before
  # declaring it done.
  local waited=0 size=0 previous=-1
  while [ $waited -lt 300 ]; do
    if [ -f "$pdf" ]; then
      size=$(wc -c < "$pdf" 2>/dev/null || echo 0)
      if [ "$size" -gt 0 ] && [ "$size" = "$previous" ]; then
        break
      fi
      previous=$size
    fi
    sleep 0.1
    waited=$((waited + 1))
  done

  kill -9 "$pid" 2>/dev/null
  wait "$pid" 2>/dev/null

  [ -s "$pdf" ]
}

# Give each file a name that says what it is. Twenty-two of them are README.md,
# so mirroring the source names would produce an unusable directory.
output_name() {
  local rel="$1"
  case "$rel" in
    README.md)                     echo "00-overview" ;;
    SPEC.md)                       echo "00-spec" ;;
    tasks/plan.md)                 echo "00-plan" ;;
    tasks/todo.md)                 echo "00-tasks" ;;
    modules/*/homework/README.md)  echo "$(basename "$(dirname "$(dirname "$rel")")")-homework" ;;
    modules/*/README.md)           echo "$(basename "$(dirname "$rel")")" ;;
    *)                             echo "$(echo "${rel%.md}" | tr '/' '-')" ;;
  esac
}

# A readable title for the PDF's first page and its document metadata.
title_for() {
  local rel="$1"
  case "$rel" in
    README.md)                     echo "Java 27 for Programmers New to Java" ;;
    SPEC.md)                       echo "Specification" ;;
    tasks/plan.md)                 echo "Implementation Plan" ;;
    tasks/todo.md)                 echo "Task Breakdown" ;;
    modules/*/homework/README.md)
      local id
      id="$(basename "$(dirname "$(dirname "$rel")")")"
      echo "Homework: ${id}" ;;
    modules/*/README.md)           echo "$(basename "$(dirname "$rel")")" ;;
    *)                             echo "${rel%.md}" ;;
  esac
}

# --- language configuration ---------------------------------------------------
# Each language is a (source prefix, output directory, book title) triple. The
# Turkish translation mirrors the English structure under tr/, so the same
# traversal serves both.
langs=()
case "${1:-}" in
  --en)   langs=("en") ;;
  --tr)   langs=("tr") ;;
  --book) langs=("en" "tr") ;;
  *)      langs=("en" "tr") ;;
esac

book_only=false
[ "${1:-}" = "--book" ] && book_only=true

made=0
failed=0

for lang in "${langs[@]}"; do

  if [ "$lang" = "tr" ]; then
    PREFIX="tr/"
    OUT_DIR="$REPO_ROOT/pdf-tr"
    BOOK_TITLE="Java 27, Java'ya Yeni Başlayan Programcılar İçin"
    BOOK_FILE="Java-27-Mufredat.pdf"
    if [ ! -d "$REPO_ROOT/tr" ]; then
      echo "No tr/ directory; skipping Turkish."
      continue
    fi
  else
    PREFIX=""
    OUT_DIR="$REPO_ROOT/pdf"
    BOOK_TITLE="Java 27 for Programmers New to Java"
    BOOK_FILE="Java-27-Curriculum.pdf"
  fi

  mkdir -p "$OUT_DIR"

  # --- collect sources --------------------------------------------------------
  # Ordered so the combined book reads front to back: overview, spec, then the
  # modules in sequence with each homework following its lesson.
  sources=()
  [ -f "$REPO_ROOT/${PREFIX}README.md" ] && sources+=("${PREFIX}README.md")
  [ -f "$REPO_ROOT/${PREFIX}CEVIRI-NOTLARI.md" ] && sources+=("${PREFIX}CEVIRI-NOTLARI.md")
  [ -f "$REPO_ROOT/${PREFIX}SPEC.md" ] && sources+=("${PREFIX}SPEC.md")
  for dir in "$REPO_ROOT/${PREFIX}modules"/*/; do
    [ -d "$dir" ] || continue
    id="$(basename "$dir")"
    [ -f "$dir/README.md" ] && sources+=("${PREFIX}modules/$id/README.md")
    [ -f "$dir/homework/README.md" ] && sources+=("${PREFIX}modules/$id/homework/README.md")
  done
  [ -f "$REPO_ROOT/${PREFIX}tasks/plan.md" ] && sources+=("${PREFIX}tasks/plan.md")
  [ -f "$REPO_ROOT/${PREFIX}tasks/todo.md" ] && sources+=("${PREFIX}tasks/todo.md")

  if [ ${#sources[@]} -eq 0 ]; then
    echo "No Markdown found for '$lang'; skipping."
    continue
  fi

  echo "[$lang] rendering ${#sources[@]} files to ${OUT_DIR#"$REPO_ROOT"/}/"
  echo

  # --- per-file PDFs ----------------------------------------------------------
  if [ "$book_only" = false ]; then
    for rel in "${sources[@]}"; do
      bare="${rel#"$PREFIX"}"
      name="$(output_name "$bare")"
      title="$(title_for "$bare")"
      html="$WORK/$lang-$name.html"
      pdf="$OUT_DIR/$name.pdf"

      # pagetitle, not title. Setting `title` makes pandoc render a heading
      # block above the content, and every one of these files already opens with
      # its own H1, so that produced the title twice. pagetitle sets only the
      # document's <title>, which becomes the PDF's metadata title.
      pandoc "$REPO_ROOT/$rel" \
          --standalone \
          --css="$CSS" \
          --embed-resources \
          --metadata pagetitle="$title" \
          --from=gfm \
          --to=html5 \
          -o "$html" 2>/dev/null

      if render "$html" "$pdf"; then
        printf '  ok    %-42s %6s KB\n' "$name.pdf" "$(( $(wc -c < "$pdf") / 1024 ))"
        made=$((made + 1))
      else
        printf '  FAIL  %s\n' "$name.pdf"
        failed=$((failed + 1))
      fi
    done
    echo
  fi

  # --- the combined book ------------------------------------------------------
  book_md="$WORK/$lang-book.md"
  : > "$book_md"

  for rel in "${sources[@]}"; do
    # A page break before each document makes each one start cleanly.
    printf '\n\n<div class="page-break"></div>\n\n' >> "$book_md"
    cat "$REPO_ROOT/$rel" >> "$book_md"
    printf '\n' >> "$book_md"
  done

  pandoc "$book_md" \
      --standalone \
      --css="$CSS" \
      --embed-resources \
      --metadata title="$BOOK_TITLE" \
      --toc --toc-depth=2 \
      --from=gfm \
      --to=html5 \
      -o "$WORK/$lang-book.html" 2>/dev/null

  if render "$WORK/$lang-book.html" "$OUT_DIR/$BOOK_FILE"; then
    printf '  ok    %-42s %6s KB\n' "$BOOK_FILE" \
        "$(( $(wc -c < "$OUT_DIR/$BOOK_FILE") / 1024 ))"
    made=$((made + 1))
  else
    printf '  FAIL  %s\n' "$BOOK_FILE"
    failed=$((failed + 1))
  fi
  echo

done

# --- summary -----------------------------------------------------------------
echo
if [ $failed -eq 0 ]; then
  echo "Done. $made PDF(s) built."
  exit 0
fi

echo "$made succeeded, $failed failed."
exit 1
