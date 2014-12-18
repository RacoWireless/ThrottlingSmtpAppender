using DuoVia.FuzzyStrings;
using log4net.Appender;
using log4net.Core;
using System;
using System.Collections.Generic;
using System.Linq;

namespace Log4Net.ThrottlingSmtpAppender
{
    public class ThrottlingSmtpAppender : SmtpAppender
    {
        private IList<SentMessage> SentMessages { get; set; }
        private double _matchCoefficient = .5;
        private int _cacheTimeoutMinutes = 5;
        private static readonly object Locker = new object();

        /// <summary>
        /// Sets the match coefficient from config
        /// </summary>
        /// <param name="value"></param>
        public void AddMatchCoefficient(double value)
        {
            if (0 < value && value < 1)
            {
                _matchCoefficient = value;
            }
        }

        public void AddCacheTimeoutMinutes(int value)
        {
            if (0 < value && value < 60)
            {
                _cacheTimeoutMinutes = value;
            }
        }

        public ThrottlingSmtpAppender()
        {
            SentMessages = new List<SentMessage>();
        }

        protected override void SendBuffer(LoggingEvent[] events)
        {
            lock (Locker)
            {
                PurgeAllOldSentMessages();

                /*
                 *  The last event is the one that exceeded the level evaluator
                 *  so we need to test that one, and if it passes, send the entire
                 *  array
                 */

                var eventToTest = events.Last();
                if (ShouldSend(eventToTest.RenderedMessage))
                {
                    base.SendBuffer(events);
                    SentMessages.Add(new SentMessage(eventToTest.RenderedMessage));
                }
            }
        }

        private bool ShouldSend(string input)
        {
            var returnVal = true;
            foreach (var sentMessage in SentMessages)
            {
                var tested = input.FuzzyMatch(sentMessage.RenderedMessage);
                if (tested > _matchCoefficient)
                {
                    returnVal = false;
                    break;
                }
            }

            return returnVal;
        }

        private void PurgeAllOldSentMessages()
        {
            SentMessages =
                SentMessages.Where(x => x.SendTime > DateTime.Now.Subtract(TimeSpan.FromMinutes(_cacheTimeoutMinutes))).ToList();
        }

        private class SentMessage
        {
            public DateTime SendTime { get; set; }
            public string RenderedMessage { get; set; }

            public SentMessage(string renderedMessage)
            {
                SendTime = DateTime.Now;
                RenderedMessage = renderedMessage;
            }
        }
    }
}
