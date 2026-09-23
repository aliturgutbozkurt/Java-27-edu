# Ödev 12: Bir Kelime Dizini

Küçük bir metin dizini kurun. Her adım bir koleksiyon seçip gerekçelendirmenizi
istiyor, çünkü bu modülün asıl öğrettiği beceri doğru seçmek.

## Metin

```java
private static final String TEXT = """
        the quick brown fox jumps over the lazy dog
        the dog barks and the fox runs
        a quick fox is a happy fox""";
```

## Birinci Bölüm: Sayma

`WordIndex.java` dosyasını klasik biçimde oluşturun.

Her kelimenin kaç kez geçtiğini sayın, sonra en sık geçen üçünü yazdırın.

Gereksinimler:

- Sayma için `merge` kullanın. `get`, null kontrolü, `put` dizisi **yazmayın**.
- Sayımlar **öngörülebilir** bir sırada yazdırılmalı, ve bir yorum hangi harita
  tipini seçtiğinizi ve `HashMap` seçiminin neden yanlış olacağını söylemeli.
- `the` ve `fox` dörder kez geçiyor. İlk üçünüz çalıştırmalar arasında
  **belirlenimci** olmalı, dolayısıyla eşitliği nasıl bozacağınıza karar verin ve
  bir yorumda söyleyin.

## İkinci Bölüm: Dizinleme

Her baş harften, o harfle başlayan kelimelerin kümesine bir dizin kurun.

Gereksinimler:

- Baş harfler ayrı bir sıralama adımı olmadan **alfabetik** çıkmalı
- Her kelime baş harf başına **bir kez** görünmeli, ve kelimeler **sıralı** olmalı
- Null kontrolü değil `computeIfAbsent` kullanın

Sonra sıralı koleksiyon metotlarıyla ilk girdiyi, son girdiyi ve anahtarları ters
sırada yazdırın.

Son olarak benzersiz kelimeler kümesini `HashSet`, `LinkedHashSet` ve `TreeSet`
ile **üç ayrı biçimde** kurun, üçünü de yazdırın ve her birinin ne söz verdiğini
bir yorumda yazın. Bunlardan biri hiçbir söz vermiyor.

## Üçüncü Bölüm: Bir Girdiyi Kaybedin

Yeniden adlandırılabilen tek bir alanı, doğru `equals` ve `hashCode` metotları ve
bir `toString` metodu olan bir `MutableTag` sınıfı yazın.

Birini bir `HashMap` içine **anahtar** olarak koyun, sonra yeniden adlandırın. Şu
sırayla yazdırın:

1. Yeniden adlandırmadan önce `get`, `containsKey` ve `size`
2. Sonrasında aynı üçü
3. Yineleyerek anahtar kümesi
4. `remove(tag)` çağrısından sonra `size`

### Açıklayın

Kendi kelimelerinizle şunları kapsayan bir yorum yazın:

- Girdi açıkça hâlâ oradayken `get` neden null döndürüyor
- Yineleme neden buluyor ama arama neden bulmuyor
- `remove` neden onu da silemiyor
- Ondan kurtulmanın geriye kalan tek yolu ne

Sonra aynısını anahtar olarak bir record ile yapın ve yeni kurulmuş eşit bir
anahtarın değeri hâlâ bulduğunu gösterin.

## Kabul Kriterleri

- [ ] `java WordIndex.java` ile çalışıyor
- [ ] Sayma `merge` kullanıyor
- [ ] Bir yorum harita tipini `HashMap` yerine gerekçelendiriyor
- [ ] İlk üç çıktısı belirlenimci, eşitlik bozma açıklanmış
- [ ] Baş harf dizini alfabetik, değerleri sıralı ve tekrarsız
- [ ] `computeIfAbsent` kullanılıyor
- [ ] `firstEntry`, `lastEntry` ve `reversed` hepsi görünüyor
- [ ] Üç küme tipi de, her birinin ne söz verdiğine dair bir yorumla yazdırılıyor
- [ ] Değiştirilebilir anahtar gösterimi `get` null, `containsKey` false, `size` 1
      gösteriyor
- [ ] Yineleme kaybolan girdiyi hâlâ veriyor, ve `remove` onu silemiyor
- [ ] Açıklama yukarıdaki dört sorunun hepsini kapsıyor

## İleri Seviye

Kaybolan girdiden kurtulun. Haritayı yeniden kurmadan, ulaşılamaz anahtarı bir
`Iterator` kullanarak silin.

Sonra `Iterator.remove` metodunun `Map.remove` metodunun yapamadığını neden
yapabildiğini bir yorumda yazın ve bunu Modül 12'nin
`ConcurrentModificationException` hakkında söylediğine, yani aynı mekanizmanın
öteki taraftan görünümü olduğuna bağlayın.

## İpucu, ilk üçünüz değişip duruyorsa

Yalnızca sayıya göre sıralamak eşit sayıları, o an hangi sıradaysalar o sırada
bırakır. O sıra bir hash tablosundan gelebilir, yani kararlı değildir. Eşitliklerin
her seferinde aynı çözülmesi için ikinci bir karşılaştırma ekleyin.
