// src/__tests__/utils/create-mock-query-client.ts
import { QueryClient } from "@tanstack/react-query";

export function createMockQueryClient(queryFn?: any) {
  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        queryFn: queryFn ?? (() => Promise.resolve(null)),
      },
    },
  });
}
