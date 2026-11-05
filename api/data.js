// Vercel Serverless Function: /api/data
// Supports Upstash Redis / Vercel KV with command-array REST API, or Memory/Seed fallback with full CORS

const SEED_DATA = {
  expenses: [
    {
      id: "exp_1793783792128_df4r",
      amount: 20000,
      note: "Mie Bangladesh",
      timestamp: "2026-11-04T09:16:19.566Z",
      categoryId: "cat_food",
      categoryName: "Makan & Minum",
      categoryIcon: "utensils"
    },
    {
      id: "exp_1793780978804_iugr",
      amount: 100000,
      note: "Five Little Pigs",
      timestamp: "2026-11-04T08:29:07.663Z",
      categoryId: "cat_custom_1793780465895",
      categoryName: "Buku",
      categoryIcon: "book"
    },
    {
      id: "exp_1793780353270_tsf6",
      amount: 25000,
      note: "Tianlala",
      timestamp: "2026-11-04T08:19:03.379Z",
      categoryId: "cat_food",
      categoryName: "Makan & Minum",
      categoryIcon: "utensils"
    },
    {
      id: "exp_1793779460273_emrk",
      amount: 76000,
      note: "Chikuro",
      timestamp: "2026-11-04T08:04:00.386Z",
      categoryId: "cat_food",
      categoryName: "Makan & Minum",
      categoryIcon: "utensils"
    },
    {
      id: "exp_1793778972943_g5rj",
      amount: 2000,
      note: "Parkir",
      timestamp: "2026-11-04T07:56:00.180Z",
      categoryId: "cat_other",
      categoryName: "Lainnya",
      categoryIcon: "sparkles"
    },
    {
      id: "exp_1793778113547_j3ir",
      amount: 61000,
      note: "Paket Data",
      timestamp: "2026-11-04T07:41:20.594Z",
      categoryId: "cat_bill",
      categoryName: "Tagihan",
      categoryIcon: "receipt"
    },
    {
      id: "exp_1793776170077_2sgf",
      amount: 24000,
      note: "Sanger Arata",
      timestamp: "2026-11-04T07:09:16.240Z",
      categoryId: "cat_coffee",
      categoryName: "Kopi & Jajan",
      categoryIcon: "coffee"
    }
  ],
  categories: [
    { id: "cat_bill", name: "Tagihan", isDefault: true, icon: "receipt" },
    { id: "cat_charity", name: "Donasi", isDefault: true, icon: "heart" },
    { id: "cat_coffee", name: "Kopi & Jajan", isDefault: true, icon: "coffee" },
    { id: "cat_edu", name: "Pendidikan", isDefault: true, icon: "book" },
    { id: "cat_food", name: "Makan & Minum", isDefault: true, icon: "utensils" },
    { id: "cat_fun", name: "Hiburan", isDefault: true, icon: "ticket" },
    { id: "cat_health", name: "Kesehatan", isDefault: true, icon: "pill" },
    { id: "cat_other", name: "Lainnya", isDefault: true, icon: "sparkles" },
    { id: "cat_shop", name: "Belanja", isDefault: true, icon: "shopping-bag" },
    { id: "cat_trans", name: "Transportasi", isDefault: true, icon: "car" },
    { id: "cat_custom_1793780465895", name: "Buku", icon: "book", isDefault: false }
  ],
  lastUpdated: new Date().toISOString()
};

// In-memory fallback
let memoryStore = { ...SEED_DATA };

function getKvCredentials() {
  const kvUrl = process.env.KV_REST_API_URL || process.env.UPSTASH_REDIS_REST_URL;
  const kvToken = process.env.KV_REST_API_TOKEN || process.env.UPSTASH_REDIS_REST_TOKEN;
  return { kvUrl, kvToken };
}

// Helper to interact with Upstash / Vercel KV REST API
async function getFromKv() {
  const { kvUrl, kvToken } = getKvCredentials();
  if (!kvUrl || !kvToken) return null;

  try {
    // 1. Try standard root command array POST ["GET", "simplenote_data"]
    const res = await fetch(`${kvUrl}/`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${kvToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(["GET", "simplenote_data"])
    });

    if (res.ok) {
      const data = await res.json();
      if (data && data.result) {
        return typeof data.result === "string" ? JSON.parse(data.result) : data.result;
      }
    }

    // 2. Fallback to path GET /get/simplenote_data
    const pathRes = await fetch(`${kvUrl}/get/simplenote_data`, {
      headers: { Authorization: `Bearer ${kvToken}` }
    });
    if (pathRes.ok) {
      const pathData = await pathRes.json();
      if (pathData && pathData.result) {
        return typeof pathData.result === "string" ? JSON.parse(pathData.result) : pathData.result;
      }
    }
  } catch (err) {
    console.error("KV read error:", err);
  }
  return null;
}

async function saveToKv(payload) {
  const { kvUrl, kvToken } = getKvCredentials();
  if (!kvUrl || !kvToken) return false;

  const serialized = JSON.stringify(payload);

  try {
    // 1. Primary: Root command array ["SET", "simplenote_data", payload_json]
    const res = await fetch(`${kvUrl}/`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${kvToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(["SET", "simplenote_data", serialized])
    });

    if (res.ok) return true;

    // 2. Fallback: Path-based POST /set/simplenote_data
    const fallbackRes = await fetch(`${kvUrl}/set/simplenote_data`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${kvToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(serialized)
    });
    return fallbackRes.ok;
  } catch (err) {
    console.error("KV write error:", err);
    return false;
  }
}

module.exports = async function handler(req, res) {
  // Set Universal CORS Headers for PWA & Android app
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  const { kvUrl } = getKvCredentials();
  const storageBackend = kvUrl ? "Upstash-KV" : "Memory-Fallback";
  res.setHeader("X-Storage-Backend", storageBackend);

  // Ping / Health Check mode
  if (req.query && req.query.ping === "true") {
    return res.status(200).json({
      status: "ok",
      appName: "SimpleNote Backend",
      timestamp: new Date().toISOString(),
      storage: storageBackend,
      kvConnected: !!kvUrl
    });
  }

  // 1. GET: Fetch data
  if (req.method === "GET") {
    let dataToReturn = await getFromKv();

    // If KV is connected but empty, seed it with initial data so it's ready
    if (!dataToReturn && kvUrl) {
      await saveToKv(SEED_DATA);
      dataToReturn = SEED_DATA;
    }

    if (!dataToReturn) {
      dataToReturn = memoryStore;
    }

    res.setHeader("Content-Type", "application/json");
    res.setHeader("Cache-Control", "no-store, max-age=0");
    return res.status(200).json(dataToReturn);
  }

  // 2. POST: Update data
  if (req.method === "POST") {
    try {
      const body = typeof req.body === "string" ? JSON.parse(req.body) : req.body;
      if (!body || !Array.isArray(body.expenses)) {
        return res.status(400).json({ error: "Invalid payload: 'expenses' array required" });
      }

      const updatedPayload = {
        expenses: body.expenses,
        categories: Array.isArray(body.categories) ? body.categories : (memoryStore.categories || SEED_DATA.categories),
        lastUpdated: new Date().toISOString()
      };

      // Update in KV if configured
      const kvSaved = await saveToKv(updatedPayload);

      // Always update in-memory store
      memoryStore = updatedPayload;

      res.setHeader("Content-Type", "application/json");
      return res.status(200).json({
        success: true,
        message: "Data saved successfully",
        storage: kvSaved ? "Upstash-KV" : "Memory-Store",
        lastUpdated: updatedPayload.lastUpdated,
        totalExpenses: updatedPayload.expenses.length
      });
    } catch (err) {
      console.error("POST handler error:", err);
      return res.status(500).json({ error: "Failed to process request: " + err.message });
    }
  }

  return res.status(405).json({ error: "Method not allowed" });
};
