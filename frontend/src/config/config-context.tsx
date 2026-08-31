import { createContext } from "react";
import type { AppConfig } from "./config-loader";

export const ConfigContext = createContext<AppConfig | null>(null);
