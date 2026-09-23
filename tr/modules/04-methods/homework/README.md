# Ödev 04: Bir Metot Neyi Değiştirebilir, Neyi Değiştiremez

İki program. Birincisi değer geçişi kuralını kendinize güvenle değil kanıtla
göstermenizi istiyor. İkincisi sizi bilerek gerçek bir üretim hatasına sokuyor.

## Birinci Bölüm: Envanter

`Inventory.java` oluşturun. Değiştirilebilir bir listeyle başlayın:

```java
var stock = new ArrayList<String>(List.of("bolt", "nut"));
```

Üç metot yazın ve hepsini çağırıp her birinden sonra listeyi yazdırın:

1. `addItem(List<String> items, String item)` — bir ürün ekler. Bu çağırana
   **görünmek zorunda**.
2. `replaceAll(List<String> items, List<String> replacements)` — parametreye yeni
   bir liste atayacak şekilde yazılmış. Bu çağırana **görünmemeli**, ve
   yazdırdığınız çıktı görünmediğini göstermeli.
3. `replaceAllProperly(...)` — düzeltilmiş sürüm.

Sonra şunu cevaplayan bir yorum yazın: `replaceAll` metodunu düzeltmenin tam
olarak iki yolu var. İkisini de isimlendirin ve her birini ne zaman
seçeceğinizi söyleyin.

## İkinci Bölüm: Remove Tuzağı

`RemoveTrap.java` oluşturun. Bir `List<Integer>` kurun ve her iki aşırı yüklemeyi
tetikleyin:

```java
var ids = new ArrayList<Integer>(List.of(100, 200, 2, 300));
int idToDrop = 2;
ids.remove(idToDrop);
```

Sonucu yazdırın. Kodun söylüyor göründüğü şey olmayacak.

Sonra **kendi kelimelerinizle**, derleyicinin neden o aşırı yüklemeyi seçtiğini
açıklayan bir yorum yazın. Açıklamanız kutulamanın çözümleme sırasında nerede
durduğundan bahsetmeli, çünkü asıl sebep o.

Kodun göründüğü işi yapmasını sağlayan en az iki yolu göstererek bitirin.

## Kabul Kriterleri

- [ ] Her iki dosya da `java Inventory.java` ve `java RemoveTrap.java` ile çalışıyor
- [ ] `Inventory.java` çıktısı `addItem` metodunun çağırana göründüğünü ve
      `replaceAll` metodunun görünmediğini kanıtlıyor
- [ ] Bir yorum `replaceAll` için iki düzeltmeyi isimlendiriyor ve hangisinin ne
      zaman geçerli olduğunu söylüyor
- [ ] `RemoveTrap.java` aynı başlangıç listesinden `remove(int)` ve
      `remove(Object)` çağrılarının farklı sonuç verdiğini gösteriyor
- [ ] Açıklama aşırı yükleme çözümleme sırasından kendi kelimelerinizle bahsediyor
- [ ] En az iki doğru alternatif gösteriliyor

## İleri Seviye

`List<String>` alanı tutan ve getter'ı alanı doğrudan döndüren küçük bir sınıf
ekleyin. `main` içinden listeyi alın, değiştirin ve nesnenin içeriğini yazdırarak
iç durumunu izinsiz olarak dışarıdan değiştirdiğinizi gösterin.

Sonra getter'ı düzeltin ve düzeltmenin bedelini söyleyin. Birden fazla düzeltme
var ve bedelleri aynı değil.

## İpucu, birinci bölüm zaten çalışıyor görünüyorsa

`replaceAll` çağırana ait listeyi değiştiriyor görünüyorsa, parametreye atama mı
yaptığınızı yoksa üzerinde bir metot mu çağırdığınızı kontrol edin. Bu ikisinden
yalnızca biri dışarıdan görünür, ve alıştırmanın tamamı hangisini yazdığınıza
bağlı.
