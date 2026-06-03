const fs = require('fs');
const path = require('path');

const COLLECTION_NAME = "Goods Price Comparison Service - Smoke Tests";
const BASE_URL = "http://localhost:8080";

const collection = {
    info: {
        _postman_id: "generated-collection-id",
        name: COLLECTION_NAME,
        schema: "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    },
    variable: [
        { key: "baseUrl", value: BASE_URL, type: "string" },
        { key: "categoryId", value: "", type: "string" },
        { key: "unitId", value: "", type: "string" },
        { key: "storeId", value: "", type: "string" },
        { key: "storeIdToDelete", value: "", type: "string" },
        { key: "productId", value: "", type: "string" },
        { key: "productIdToDelete", value: "", type: "string" },
        { key: "priceId", value: "", type: "string" },
        { key: "priceIdToDelete", value: "", type: "string" },
        { key: "feedbackQuestionId", value: "", type: "string" }
    ],
    item: []
};

function folder(name) { return { name, item: [] }; }

// Standard request builder. strict=true checks 2xx, strict=false just logs
function req(name, method, url, body, extraTests, strict = true) {
    const r = {
        name, request: {
            method, header: [{ key: "Content-Type", value: "application/json" }],
            url: { raw: `{{baseUrl}}${url}`, host: ["{{baseUrl}}"], path: url.split('/').filter(Boolean) }
        }, response: []
    };
    if (body) r.request.body = { mode: "raw", raw: JSON.stringify(body, null, 2) };
    const t = strict ? "pm.expect(pm.response.code).to.be.within(200,299)" : "true";
    const tests = [`pm.test('${strict?"success":"info"}',()=>${t});`];
    if (extraTests) tests.push(...extraTests);
    r.event = [{ listen: "test", script: { type: "text/javascript", exec: tests } }];
    return r;
}

const cap = v => `pm.collectionVariables.set('${v}',String(pm.response.json().id));`;

// === System ===
const s = folder("System");
s.item.push(req("Version","GET","/v1/version"));
s.item.push(req("Health","GET","/v1/health"));
s.item.push(req("Metrics","GET","/v1/metrics"));
collection.item.push(s);

// === Categories ===
const c = folder("Categories");
c.item.push(req("Create","POST","/v1/categories",{id:"TEST_CAT",name:"Test Category",description:"Testing"},[cap('categoryId')]));
c.item.push(req("List","GET","/v1/categories"));
c.item.push(req("Get","GET","/v1/categories/{{categoryId}}"));
c.item.push(req("Update","PUT","/v1/categories/{{categoryId}}",{name:"Updated",description:"Updated"}));
collection.item.push(c);

// === Units ===
const u = folder("Units");
u.item.push(req("Create","POST","/v1/units",{id:"KG",name:"Kilogram",symbol:"kg",type:"WEIGHT"},[cap('unitId')]));
u.item.push(req("List","GET","/v1/units"));
u.item.push(req("Get","GET","/v1/units/{{unitId}}"));
u.item.push(req("Update","PUT","/v1/units/{{unitId}}",{name:"Gram",symbol:"g",type:"WEIGHT"}));
collection.item.push(u);

// === Stores ===
const st = folder("Stores");
st.item.push(req("Create","POST","/v1/stores",{name:"Test Store",location:"Test Loc",chain:"Test"},[cap('storeId')]));
st.item.push(req("List","GET","/v1/stores"));
st.item.push(req("Get","GET","/v1/stores/{{storeId}}"));
st.item.push(req("Update","PUT","/v1/stores/{{storeId}}",{name:"Updated",location:"Upd",chain:"Upd"}));
st.item.push(req("Create Del","POST","/v1/stores",{name:"Del Store",location:"Del",chain:"Del"},[cap('storeIdToDelete')]));
st.item.push(req("Delete","DELETE","/v1/stores/{{storeIdToDelete}}"));
collection.item.push(st);

// === Products ===
const p = folder("Products");
p.item.push(req("Create","POST","/v1/products",{name:"Test Product",brand:"Test",category:"TEST_CAT",unit:"KG"},[cap('productId')]));
p.item.push(req("List","GET","/v1/products"));
p.item.push(req("Get","GET","/v1/products/{{productId}}"));
p.item.push(req("Update","PUT","/v1/products/{{productId}}",{name:"Updated",brand:"Upd",category:"TEST_CAT",unit:"KG"}));
p.item.push(req("Create Del","POST","/v1/products",{name:"Del Prod",brand:"Del",category:"TEST_CAT",unit:"KG"},[cap('productIdToDelete')]));
p.item.push(req("Delete","DELETE","/v1/products/{{productIdToDelete}}"));
p.item.push(req("Trend","GET","/v1/products/trend/{{productId}}?startDate=2026-01-01&endDate=2026-12-31&granularity=MONTHLY"));
collection.item.push(p);

// === Prices ===
const pr = folder("Prices");
pr.item.push(req("Create","POST","/v1/products/{{productId}}/prices",{storeId:"{{storeId}}",price:10.0,isPromo:false,dateRecorded:"2026-06-03T00:00:00Z"},[cap('priceId')]));
pr.item.push(req("List","GET","/v1/products/{{productId}}/prices"));
pr.item.push(req("Get","GET","/v1/prices/{{priceId}}"));
pr.item.push(req("Update","PUT","/v1/prices/{{priceId}}",{price:12.0,isPromo:false}));
pr.item.push(req("Create Del","POST","/v1/products/{{productId}}/prices",{storeId:"{{storeId}}",price:15.0,isPromo:false,dateRecorded:"2026-06-03T00:00:00Z"},[cap('priceIdToDelete')]));
pr.item.push(req("Delete","DELETE","/v1/prices/{{priceIdToDelete}}"));
pr.item.push(req("Search v1","POST","/v1/prices/search",{productName:"Test Product"}));
pr.item.push(req("Search v2","POST","/v2/prices/search",{productName:"Test Product"}));
collection.item.push(pr);

// === Shopping ===
const sh = folder("Shopping");
sh.item.push(req("Optimize","POST","/v1/shopping/optimize",{items:["Test Product"]}));
collection.item.push(sh);

// === Feedback ===
const fb = folder("Feedback");
fb.item.push(req("Create","POST","/v1/feedback-questions",{userName:"Test",userEmail:"t@t.com",message:"How do I compare?",type:"question"},[cap('feedbackQuestionId')]));
fb.item.push(req("List","GET","/v1/feedback-questions"));
fb.item.push(req("Get","GET","/v1/feedback-questions/{{feedbackQuestionId}}"));
collection.item.push(fb);

// === Activity Logs ===
const ac = folder("Activity Logs");
ac.item.push(req("List","GET","/v1/activity-logs"));
collection.item.push(ac);

// === Admin ===
const ad = folder("Admin");
ad.item.push(req("Trigger Job","POST","/v1/admin/jobs/price-summary-update"));
collection.item.push(ad);

// Write
fs.writeFileSync(path.join(__dirname, 'Goods Price Comparison Service.postman_collection.json'), JSON.stringify(collection, null, 2));
console.log("Generated! All endpoints check 2xx success only.");
