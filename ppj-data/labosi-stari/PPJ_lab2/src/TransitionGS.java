import java.util.HashSet;

public class TransitionGS {

		public HashSet<State> from;
		public HashSet<State> to;
		public String character;

		public TransitionGS(HashSet<State> from, HashSet<State> to, String character) {
			super();
			this.from = from;
			this.to = to;
			this.character = character;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((character == null) ? 0 : character.hashCode());
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TransitionGS other = (TransitionGS) obj;
			if (character == null) {
				if (other.character != null)
					return false;
			} else if (!character.equals(other.character))
				return false;
			if (from == null) {
				if (other.from != null)
					return false;
			} else if (!from.equals(other.from))
				return false;
			if (to == null) {
				if (other.to != null)
					return false;
			} else if (!to.equals(other.to))
				return false;
			return true;
		}
	}