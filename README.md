# archiver
Link crawler pro archivování webů do [Wayback Machine](https://web.archive.org/).

## Fáze 1
Získání URL adres všech stránek projektu Ubuntu.cz.

```bash
mvn test
mvn compile exec:java
```

Pokud chcete získat adresy z vlastních stránek, předejte jejich seznam pomocí `-Dexec.args="..."`.

```bash
mvn compile exec:java -Dexec.args="https://example.com/ https://www.example.com/ https://foo.example.com/"
```

## Fáze 2
Nahrání obsahu stránek do archivu Wayback Machine.

```bash
for file in ./out/*.txt; do
    bash scripts/archive-to-waybackmachine.sh "$file"
done
```
