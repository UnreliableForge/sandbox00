// src/config/configLoader.ts

import { load } from "js-yaml";

export type AppConfig = {
  apiBaseUrl: string;
};

let cachedConfig: AppConfig | null = null;

export async function loadConfig(): Promise<AppConfig> {
  if (cachedConfig) return cachedConfig;

  const res = await fetch("/config/config.yaml");

  // const res = await fetch("/config/config.json");
  if (!res.ok) {
    throw new Error("Failed to load config file.");
  }

  cachedConfig = load(await res.text()) as AppConfig;
  if (!cachedConfig) {
    throw new Error("Failed to load config file.");
  }

  return cachedConfig;
}
