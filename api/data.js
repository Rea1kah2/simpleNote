// Vercel Serverless Function: /api/data
// Supports Upstash Redis / Vercel KV, Supabase, or Memory/Seed fallback with full CORS

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

// Helper to interact with Upstash / Vercel KV REST API
async function getFromKv() {
  const kvUrl = process.env.KV_REST_API_URL || process.env.UPSTASH_REDIS_REST_URL;
  const kvToken = process.env.KV_REST_API_TOKEN || process.env.UPSTASH_REDIS_REST_TOKEN;

  if (!kvUrl || !kvToken) return null;

  try {
    const res = await fetch(`${kvUrl}/get/simplenote_data`, {
      headers: { Authorization: `Bearer ${kvToken}` }
    });
    if (res.ok) {
      const data = await res.json();
      if (data && data.result) {
        return typeof data.result === "string" ? JSON.parse(data.result) : data.result;
      }
    }
  } catch (err) {
    console.error("KV read error:", err);
  }
  return null;
}

async function saveToKv(payload) {
  const kvUrl = process.env.KV_REST_API_URL || process.env.UPSTASH_REDIS_REST_URL;
  const kvToken = process.env.KV_REST_API_TOKEN || process.env.UPSTASH_REDIS_REST_TOKEN;

  if (!kvUrl || !kvToken) return false;

  try {
    const res = await fetch(`${kvUrl}/set/simplenote_data`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${kvToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(JSON.stringify(payload))
    });
    return res.ok;
  } catch (err) {
    console.error("KV write error:", err);
    return false;
  }
}

module.exports = async function handler(req, res) {
  // Set Universal CORS Headers for PWA & Android app
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    return res.status(200).end();
  }

  // 1. GET: Fetch data
  if (req.method === "GET") {
    const kvData = await getFromKv();
    const dataToReturn = kvData || memoryStore;
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
      await saveToKv(updatedPayload);

      // Always update in-memory store
      memoryStore = updatedPayload;

      res.setHeader("Content-Type", "application/json");
      return res.status(200).json({
        success: true,
        message: "Data saved successfully",
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
