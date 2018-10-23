using System;
using System.Collections.Generic;
using Server.Context;

namespace Server.Models
{
    public class Messages
    {
        public int Id { get; set; }

        public DateTime TimeStamp { get; set; }

        public string Content { get; set; }

        public User user { get; set; }
    }
}