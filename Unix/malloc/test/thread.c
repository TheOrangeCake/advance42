/* ************************************************************************** */
/*                                                                            */
/*                                                        :::      ::::::::   */
/*   thread.c                                           :+:      :+:    :+:   */
/*                                                    +:+ +:+         +:+     */
/*   By: hoannguy <hoannguy@student.42lausanne.c    +#+  +:+       +#+        */
/*                                                +#+#+#+#+#+   +#+           */
/*   Created: 2026/11/22 08:50:20 by hoannguy          #+#    #+#             */
/*   Updated: 2026/11/22 09:27:43 by hoannguy         ###   ########.fr       */
/*                                                                            */
/* ************************************************************************** */

#include "libft_malloc.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#define N_THREADS 4
#define N_PTRS 10
#define N_ITER 20000
#define MAX_SIZE 1024

typedef struct s_worker {
    pthread_t   thread;
    int         id;
    char        letter;
    char        *ptr[N_PTRS];
    size_t      len[N_PTRS];
    int         n_malloc;
    int         n_calloc;
    int         n_realloc;
    int         n_free;
    int         errors;
} t_worker;

static pthread_barrier_t g_barrier;

static void print(const char *fmt, ...) {
    char    buf[512];
    va_list args;
    int     len;

    va_start(args, fmt);
    len = vsnprintf(buf, sizeof(buf), fmt, args);
    va_end(args);
    if (len > (int)sizeof(buf) - 1) {
        len = sizeof(buf) - 1;
    }
    write(1, buf, len);
}

static void title(const char *msg) {
    print("\n==================== %s ====================\n", msg);
}

// Block must still be full of our letter, else another thread wrote in it
static void check_block(t_worker *w, int i) {
    for (size_t j = 0; j < w->len[i]; j++) {
        if (w->ptr[i][j] != w->letter) {
            w->errors++;
            return;
        }
    }
}

static void do_alloc(t_worker *w, int i, size_t size, unsigned int *seed) {
    if (rand_r(seed) % 2 == 0) {
        w->ptr[i] = malloc(size);
        w->n_malloc++;
    } else {
        w->ptr[i] = calloc(size, 1);
        w->n_calloc++;
        for (size_t j = 0; w->ptr[i] && j < size; j++) {
            if (w->ptr[i][j] != 0) {
                w->errors++;
                break;
            }
        }
    }
}

// Used slot -> 1/3 free, 2/3 realloc to a random size
// Not used -> 1/2 alloc, 1/2 calloc with a random size
static void do_step(t_worker *w, unsigned int *seed) {
    int     i;
    size_t  size;

    i = rand_r(seed) % N_PTRS;
    size = 1 + rand_r(seed) % MAX_SIZE;
    if (!w->ptr[i]) {
        do_alloc(w, i, size, seed);
    } else {
        check_block(w, i);
        if (rand_r(seed) % 3 == 0) {
            free(w->ptr[i]);
            w->ptr[i] = NULL;
            w->len[i] = 0;
            w->n_free++;
            return;
        }
        w->ptr[i] = realloc(w->ptr[i], size);
        w->n_realloc++;
    }
    if (!w->ptr[i]) {
        w->errors++;
        w->len[i] = 0;
        return;
    }
    w->len[i] = size;
    memset(w->ptr[i], w->letter, size);
}

static void *routine(void *arg) {
    t_worker        *w;
    unsigned int    seed;

    w = arg;
    seed = w->id + 1;

    // first wait so all worker init
    pthread_barrier_wait(&g_barrier);
    for (int it = 0; it < N_ITER; it++) {
        do_step(w, &seed);
    }

    // second wait all worker working
    pthread_barrier_wait(&g_barrier);
    // third wait for main to print current state
    pthread_barrier_wait(&g_barrier);
    for (int i = 0; i < N_PTRS; i++) {
        if (w->ptr[i]) {
            check_block(w, i);
        }
        free(w->ptr[i]);
        w->ptr[i] = NULL;
    }
    return NULL;
}

static int print_result(t_worker *workers) {
    int     total;

    total = 0;
    title("RESULT");
    for (int i = 0; i < N_THREADS; i++) {
        print("  Thread %c: %5d malloc  %5d calloc  %5d realloc  %5d free  "
            "-> %d errors\n", workers[i].letter, workers[i].n_malloc,
            workers[i].n_calloc, workers[i].n_realloc, workers[i].n_free,
            workers[i].errors);
        total += workers[i].errors;
    }
    if (total == 0) {
        print("ALL TESTS PASSED\n");
        return 0;
    }
    print("SOME TESTS FAILED\n");
    return 1;
}

int main(void) {
    t_worker    workers[N_THREADS];

    title("4 THREADS, RANDOM MALLOC / CALLOC / REALLOC / FREE");
    print("  %d threads x %d operations, sizes 1 to %d (TINY + SMALL)\n",
        N_THREADS, N_ITER, MAX_SIZE);

    memset(workers, 0, sizeof(workers));
    pthread_barrier_init(&g_barrier, NULL, N_THREADS + 1);
    for (int i = 0; i < N_THREADS; i++) {
        workers[i].id = i;
        workers[i].letter = 'A' + i;
        pthread_create(&workers[i].thread, NULL, routine, &workers[i]);
    }
    // first wait all worker init
    pthread_barrier_wait(&g_barrier);

    // second wait all worker working
    pthread_barrier_wait(&g_barrier);
    title("HEAP AFTER THE STRESS (blocks of all threads mixed)");
    show_alloc_mem();
    // third wait for main to print current state
    pthread_barrier_wait(&g_barrier);

    for (int i = 0; i < N_THREADS; i++) {
        pthread_join(workers[i].thread, NULL);
    }
    pthread_barrier_destroy(&g_barrier);

    title("HEAP AFTER FREE");
    show_alloc_mem();
    return print_result(workers);
}
