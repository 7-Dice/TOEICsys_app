package beans;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor

public class PassageGroup implements Serializable
{
	private long groupId;
	private int partType;
	private String groupType;
	private String title;
	private List<PassageGroupItem> items;

	public int getItemCount() {
		return items == null ? 0 : items.size();
	}
}