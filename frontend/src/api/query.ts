import { useQuery, type UseQueryOptions } from "@tanstack/react-query";
import type { ApiMap } from "./ApiMap";

export function useApiQuery<K extends keyof ApiMap>(
  [key, input]: [K, ApiMap[K]["input"]],
  options?: Omit<UseQueryOptions<ApiMap[K]["output"]>, "queryKey">,
) {
  return useQuery<ApiMap[K]["output"]>({
    queryKey: [key, input],
    ...options,
  });
}
