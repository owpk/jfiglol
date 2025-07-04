package com.owpk.renderer;

import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Vorobyev Vyacheslav
 */
public class RenderEngine {

   public static void render(Renderable renderable) {
      var renderContent = renderable.getRenderContent();
      System.out.print(renderContent);
   }

   public static record QEntry(String content, String clear) {
   }

   public static void renderWithFixedFrameRate(Renderable renderable, Integer frameRate) {
      var generateFrames = 100;
      BlockingQueue<QEntry> queue = new LinkedBlockingQueue<>();
      var scheduler = Executors.newScheduledThreadPool(1);
      var producerExecutor = Executors.newSingleThreadExecutor();
      var atomicStart = new AtomicBoolean(false);

      producerExecutor.submit(() -> {
         for (int i = 0; i < generateFrames; i++) {
            var content = renderable.getRenderContent();
            var clear = clearScreen(getLinesHeightOfContent(content));
            try {
               queue.put(new QEntry(content, clear));
            } catch (Exception ignore) {
            }
         }
      });

      var sleepTime = 1000 / frameRate;

      // Consumer loop
      Runnable consumerTask = new Runnable() {
         @Override
         public void run() {
            var entry = queue.poll();
            if (entry != null) {
               if (atomicStart.getAndSet(true))
                  System.out.print(entry.clear());
               System.out.print(entry.content());
               queue.offer(entry); // Return to the end of the queue
            }
            scheduler.schedule(this, sleepTime, TimeUnit.MILLISECONDS); // re-schedule
         }
      };

      // Start consumer loop
      scheduler.schedule(consumerTask, sleepTime, TimeUnit.MILLISECONDS);
   }

   private static Long getLinesHeightOfContent(String content) {
      return content.lines().count();
   }

   public static String clearScreen(Long height) {
      var writer = new StringWriter();
      for (int i = 0; i < height; i++) {
         writer.write(String.format("\u001b[%dA", 1));
         writer.write("\u001b[2K");
      }
      return writer.toString();
   }
}
