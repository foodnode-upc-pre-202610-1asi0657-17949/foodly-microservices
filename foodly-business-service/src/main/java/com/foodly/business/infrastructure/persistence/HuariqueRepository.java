package com.foodly.business.infrastructure.persistence;

import com.foodly.business.domain.model.DaySchedule;
import com.foodly.business.domain.model.Huarique;
import com.foodly.business.domain.model.Menu;
import com.foodly.business.domain.model.Product;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HuariqueRepository {

    private static final String DEFAULT_MONGO_URI =
            "mongodb://foodly:f00dl1@ac-jynysjh-shard-00-00.hrszyjl.mongodb.net:27017,ac-jynysjh-shard-00-01.hrszyjl.mongodb.net:27017,ac-jynysjh-shard-00-02.hrszyjl.mongodb.net:27017/?ssl=true&replicaSet=atlas-qqsnnj-shard-0&authSource=admin&appName=Foodly";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @PostConstruct
    public void init() {
        String uri = System.getenv("MONGO_URI");
        if (uri == null || uri.isBlank()) {
            uri = DEFAULT_MONGO_URI;
        }
        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase("foodly_business");
        this.collection = database.getCollection("huariques");
    }

    public List<Huarique> findAll() {
        List<Huarique> list = new ArrayList<>();
        List<Document> docs = collection.find().into(new ArrayList<>());
        for (Document doc : docs) {
            list.add(mapToEntity(doc));
        }
        return list;
    }

    public Optional<Huarique> findById(String id) {
        try {
            Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            if (doc != null) {
                return Optional.of(mapToEntity(doc));
            }
        } catch (IllegalArgumentException e) {
            // id con formato inválido para ObjectId -> no encontrado
        }
        return Optional.empty();
    }

    public Optional<Huarique> findByOwnerId(String ownerId) {
        Document doc = collection.find(Filters.eq("ownerId", ownerId)).first();
        if (doc != null) {
            return Optional.of(mapToEntity(doc));
        }
        return Optional.empty();
    }

    public Huarique save(Huarique huarique) {
        Document doc = new Document();

        if (huarique.getId() != null && !huarique.getId().isEmpty()) {
            doc.append("_id", new ObjectId(huarique.getId()));
        } else {
            huarique.setId(new ObjectId().toHexString());
            doc.append("_id", new ObjectId(huarique.getId()));
        }

        doc.append("ownerId", huarique.getOwnerId())
                .append("name", huarique.getName())
                .append("address", huarique.getAddress())
                .append("h3Index", huarique.getH3Index())
                .append("latitude", huarique.getLatitude())
                .append("longitude", huarique.getLongitude())
                .append("cuisineType", huarique.getCuisineType())
                .append("phone", huarique.getPhone())
                .append("priceRange", huarique.getPriceRange())
                .append("isOpen", huarique.getIsOpen())
                .append("photos", huarique.getPhotos());

        if (huarique.getSchedule() != null) {
            List<Document> scheduleDocs = new ArrayList<>();
            for (DaySchedule d : huarique.getSchedule()) {
                scheduleDocs.add(new Document("day", d.getDay())
                        .append("open", d.getOpen())
                        .append("from", d.getFrom())
                        .append("to", d.getTo()));
            }
            doc.append("schedule", scheduleDocs);
        }

        if (huarique.getMenu() != null) {
            List<Document> productDocs = new ArrayList<>();
            for (Product p : huarique.getMenu().getProducts()) {
                productDocs.add(new Document("_id", p.getId())
                        .append("name", p.getName())
                        .append("description", p.getDescription())
                        .append("price", p.getPrice())
                        .append("available", p.getAvailable())
                        .append("imageUrl", p.getImageUrl()));
            }

            Document menuDoc = new Document("categories", huarique.getMenu().getCategories())
                    .append("products", productDocs);
            doc.append("menu", menuDoc);
        }

        collection.replaceOne(Filters.eq("_id", new ObjectId(huarique.getId())), doc,
                new ReplaceOptions().upsert(true));

        return huarique;
    }

    private Huarique mapToEntity(Document doc) {
        Huarique h = new Huarique();
        h.setId(doc.getObjectId("_id").toHexString());
        h.setOwnerId(doc.getString("ownerId"));
        h.setName(doc.getString("name"));
        h.setAddress(doc.getString("address"));
        h.setH3Index(doc.getString("h3Index"));
        h.setLatitude(doc.getDouble("latitude"));
        h.setLongitude(doc.getDouble("longitude"));
        h.setCuisineType(doc.getString("cuisineType"));
        h.setPhone(doc.getString("phone"));
        h.setPriceRange(doc.getString("priceRange"));

        Boolean isOpen = doc.getBoolean("isOpen");
        h.setIsOpen(isOpen != null ? isOpen : true);

        List<String> photos = doc.getList("photos", String.class);
        h.setPhotos(photos != null ? photos : new ArrayList<>());

        List<Document> scheduleDocs = doc.getList("schedule", Document.class);
        if (scheduleDocs != null) {
            List<DaySchedule> schedule = new ArrayList<>();
            for (Document sDoc : scheduleDocs) {
                schedule.add(new DaySchedule(
                        sDoc.getString("day"),
                        sDoc.getBoolean("open"),
                        sDoc.getString("from"),
                        sDoc.getString("to")
                ));
            }
            h.setSchedule(schedule);
        }

        Document menuDoc = doc.get("menu", Document.class);
        if (menuDoc != null) {
            Menu menu = new Menu();
            menu.setCategories(menuDoc.getList("categories", String.class));

            List<Document> productDocs = menuDoc.getList("products", Document.class);
            if (productDocs != null) {
                List<Product> products = new ArrayList<>();
                for (Document pDoc : productDocs) {
                    Product p = new Product();
                    p.setId(pDoc.getString("_id"));
                    p.setName(pDoc.getString("name"));
                    p.setDescription(pDoc.getString("description"));
                    p.setPrice(pDoc.getDouble("price"));
                    p.setAvailable(pDoc.getBoolean("available"));
                    p.setImageUrl(pDoc.getString("imageUrl"));
                    products.add(p);
                }
                menu.setProducts(products);
            }
            h.setMenu(menu);
        }
        return h;
    }

    @PreDestroy
    public void close() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }
}